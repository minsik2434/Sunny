package com.sunny.alarmservice.common;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtUtil {

    private final Key key;
    public JwtUtil(@Value("${jwt.secret}") String keyString){
        byte[] keyByte = Decoders.BASE64.decode(keyString);
        key = Keys.hmacShaKeyFor(keyByte);
    }

    public String getClaim(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().get("user_email");
    }
}
