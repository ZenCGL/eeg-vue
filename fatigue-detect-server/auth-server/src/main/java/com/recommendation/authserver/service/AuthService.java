package com.recommendation.authserver.service;

import com.recommendation.common.utils.TokenUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {
    TokenUtil tokenUtil = new TokenUtil();
    public Cookie generateToken(String userId){
        Cookie cookie = new Cookie("token", tokenUtil.getToken(userId));
        cookie.setMaxAge(7*24*60*60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        return cookie;
    }
    public String getIdFromToken(String token){
        Map<String, String> map = tokenUtil.parseToken(token);
        String userId = map.get("userId");
        return userId;
    }
}
