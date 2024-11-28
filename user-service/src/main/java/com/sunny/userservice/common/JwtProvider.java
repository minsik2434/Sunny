package com.sunny.userservice.common;

import com.sunny.userservice.dto.TokenResponseDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;

public class JwtProvider {
    @Value("${jwt.access-token-expiration}")
    private long ACCESS_TOKEN_EXPIRATION_TIME;

    @Value("${jwt.refresh-token-expiration}")
    private long REFRESH_TOKEN_EXPIRATION_TIME;

    private final Key key;
    public JwtProvider(String keyString){
        byte[] keyByte = Decoders.BASE64.decode(keyString);
        key = Keys.hmacShaKeyFor(keyByte);
    }

    public TokenResponseDto createToken(String claim){
        long now = System.currentTimeMillis();
        String accessToken = genAccessToken(claim, now);
        String refreshToken = genRefreshToken(claim, now);
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    public String getClaim(String token) {
        return (String) Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody().get("user_email");
    }
    private String genAccessToken(String claim, long now){
        return Jwts.builder()
                    .setSubject("AccessToken")
                    .claim("user_email", claim)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + ACCESS_TOKEN_EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }
    private String genRefreshToken(String claim ,long now){

        return Jwts.builder()
                    .setSubject("RefreshToken")
                    .claim("user_email", claim)
                    .setIssuedAt(new Date(now))
                    .setExpiration(new Date(now + REFRESH_TOKEN_EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }
}
