package com.recommendation.gateway.Filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.recommendation.common.utils.TokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Order(1)
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final TokenUtil tokenUtil;

    public AuthGlobalFilter() {
        this.tokenUtil = new TokenUtil();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestPath = request.getPath().toString();

        // 登录路径，我们不想过滤这个路径
        if (requestPath.contains("/auth/") || requestPath.contains("login") || requestPath.contains("register")) {
            // 直接传递给链中的下一个过滤器（如果有的话），或者后端服务
            return chain.filter(exchange);
        }
        MultiValueMap<String, HttpCookie> cookies = request.getCookies();
        String token = null;
       if(cookies.containsKey("token")){
            token = cookies.get("token").toString();
           token = token.substring(7);
       }
        if (null == token || "".equals(token.trim())) {
            // 处理未登录的情况，例如重定向到登录页面
            return createUnauthorizedResponse(exchange);
        }
        Map<String, String> map=new HashMap<>();
        try{
         map = tokenUtil.parseToken(token);
        }
        catch (TokenExpiredException e){
            return createUnauthorizedResponse(exchange);
        }
        String userId = map.get("userId");
        long timeOfUse = System.currentTimeMillis() - Long.parseLong(map.get("timeStamp"));
        long refreshTime = 24 * 60 * 60 * 1000L;
        long expiresTime = 24 * 60 * 60 * 1000L * 7;
        HttpCookie user_cookie = new HttpCookie("user_id", userId);
        if (timeOfUse < refreshTime) {
            // token有效，继续处理请求
            exchange = addCookie(request,user_cookie,exchange);
            return chain.filter(exchange);
        } else if (timeOfUse >= refreshTime && timeOfUse < expiresTime) {
            // token需要刷新
            ServerHttpResponse response =  exchange.getResponse();
            String newToken = tokenUtil.getToken(userId);

            ResponseCookie cookie = ResponseCookie.from("token", newToken)
                    .maxAge(7*24*60*60*1000)
                    // 其他cookie属性可以根据需要设置，例如path, domain, secure, HttpOnly等
                    .path("/")
                    .httpOnly(true)
                    .sameSite("Lax") // 或者使用 "Strict", "None"
                    .build();
            exchange.getResponse().addCookie(cookie);
            exchange = addCookie(request,user_cookie,exchange);
            return chain.filter(exchange);
        } else {
            // token已过期
            return createUnauthorizedResponse(exchange);
        }
    }
    private ServerWebExchange addCookie(ServerHttpRequest request,HttpCookie cookie,ServerWebExchange exchange){


        // 构建cookie头的字符串
        String cookieHeader = cookie.toString();

        // 使用mutate方法修改请求，添加cookie头
        ServerHttpRequest modifiedRequest = request.mutate().header("Cookie", cookieHeader).build();

        // 创建包含修改后的请求的新的ServerWebExchange
        ServerWebExchange modifiedExchange = exchange.mutate().request(modifiedRequest).build();
        return modifiedExchange;
    }
    private Mono<Void> createUnauthorizedResponse(ServerWebExchange exchange) {
        ServerHttpResponse response = exchange.getResponse();
        // 重定向到登录页面
        String loginUrl =  "/page/login"; // 登录页面的URL

        // 设置状态码为302（临时重定向）
        response.setStatusCode(HttpStatus.FOUND);

        // 设置Location头，告诉客户端重定向到的位置
        response.getHeaders().setLocation(URI.create(loginUrl));

        // 构建一个空的响应体，因为我们不需要在重定向响应中发送任何数据
        DataBuffer buffer = response.bufferFactory().wrap(new byte[0]);
        return response.writeWith(Mono.just(buffer));

    }

    @Override
    public int getOrder() {
        return -100;
    }
}