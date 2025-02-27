package com.recommendation.gateway.tmp;

import com.recommendation.common.utils.TokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Slf4j
//@Component
public class AuthHandlerInterceptor implements HandlerInterceptor {
//    @Autowired
    TokenUtil tokenUtil = new TokenUtil();
//    @Value("${token.refreshTime}")
    private Long refreshTime = (long) (24*60*60*1000);
//    @Value("${token.expiresTime}")
    private Long expiresTime = (long) (24*60*60*1000*7);
    /**
     * 权限认证的拦截操作.
     */
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) throws Exception {
        log.info("=======进入拦截器========");
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        //为空就返回错误
        Cookie[] cookies = httpServletRequest.getCookies();

        String token = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    // 找到了特定的cookie
                    token = cookie.getValue();
                    // 你可以使用cookieValue做一些操作
                    break;
                }
            }
        }
        if (null == token || "".equals(token.trim())) {
            httpServletResponse.setStatus(302);
            httpServletResponse.setHeader("location","/auth/login");
//简单的重定向⽅法
            httpServletResponse.sendRedirect("/auth/login");
            return  false;
        }
        log.info("==============token:" + token);
        Map<String, String> map = tokenUtil.parseToken(token);
        String userId = map.get("userId");
//        String userRole = map.get("userRole");
        long timeOfUse = System.currentTimeMillis() - Long.parseLong(map.get("timeStamp"));
        //1.判断 token 是否过期
        if (timeOfUse < refreshTime) {
            log.info("token验证成功");
            return true;
        }
        //超过token刷新时间，刷新 token
        else if (timeOfUse >= refreshTime && timeOfUse < expiresTime) {
//            httpServletResponse.setHeader("token",tokenUtil.getToken(userId));

            httpServletResponse.addCookie(new Cookie("token",tokenUtil.getToken(userId)));
            log.info("token刷新成功");
            httpServletRequest.removeAttribute("token");
            return true;
        }
        //token过期就返回 token 无效.
        else {
            httpServletResponse.setStatus(302);
            httpServletResponse.setHeader("location","/auth/login");
//简单的重定向⽅法
            httpServletResponse.sendRedirect("/auth/login");
            return false;
        }
    }
}