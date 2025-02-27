package com.recommendation.gateway.config;

import com.recommendation.gateway.tmp.AuthHandlerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//
//    @Autowired
//    AuthHandlerInterceptor authHandlerInterceptor;
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(authHandlerInterceptor)
//                // 可以在这里指定拦截器的路径模式，例如拦截所有请求："/**"
//                .addPathPatterns("/**"); // 拦截所有请求
//                // 也可以排除某些路径不被拦截
////                .excludePathPatterns("/auth/**", "/static/**");
//    }
//}