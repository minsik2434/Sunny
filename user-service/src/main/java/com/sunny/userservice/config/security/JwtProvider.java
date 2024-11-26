package com.sunny.userservice.config.security;

import com.sunny.userservice.dto.TokenResponseDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;

public class JwtProvider {
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = 3600000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = 80000000;

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
