package com.tontin.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expire}")
    private long expire;
    
    /**
     * 生成token
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expireTime = new Date(now.getTime() + expire * 1000);
        
        return JWT.create()
                .withClaim("userId", userId)
                .withIssuedAt(now)
                .withExpiresAt(expireTime)
                .sign(Algorithm.HMAC256(secret));
    }
    
    /**
     * 验证token
     */
    public boolean verifyToken(String token) {
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }
    
    /**
     * 从token中获取userId
     */
    public Long getUserIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("userId").asLong();
        } catch (JWTDecodeException e) {
            return null;
        }
    }
}
