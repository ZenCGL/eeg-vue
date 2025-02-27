package com.recommendation.common.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {


    private static final long EXPIRE_TIME= 7*24*60*60*1000;
    private static final String secretKey= "sdajnsdnadassad";
    /**
     * 加密token.
     */
    public String getToken(String userId) throws TokenExpiredException {

        Date expireAt=new Date(System.currentTimeMillis()+EXPIRE_TIME);

        String token = JWT
                .create()
                .withClaim("userId" ,userId)
//                .withClaim("userRole", userRole)
                .withClaim("timeStamp", System.currentTimeMillis())
                .withExpiresAt(expireAt)
                .sign(Algorithm.HMAC256(secretKey));
        return token;
    }
    /**
     * 解析token.
     * {
     * "userId": "weizhong",
     * "userRole": "ROLE_ADMIN",
     * "timeStamp": "134143214"
     * }
     */
    public Map<String, String> parseToken(String token) {
        HashMap<String, String> map = new HashMap<>();
        DecodedJWT decodedjwt = JWT.require(Algorithm.HMAC256(secretKey))
                .build().verify(token);
        Claim userId = decodedjwt.getClaim("userId");
//        Claim userRole = decodedjwt.getClaim("userRole");
        Claim timeStamp = decodedjwt.getClaim("timeStamp");
        Date expireAt =  decodedjwt.getExpiresAt();
        map.put("userId", userId.asString());
//        map.put("userRole", userRole.asString());
        map.put("timeStamp", timeStamp.asLong().toString());
//        map.put("")
        return map;
    }
}