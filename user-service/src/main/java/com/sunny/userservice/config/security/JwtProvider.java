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
    private static final long ISSUE_AT = System.currentTimeMillis();
    private static final long ACCESS_TOKEN_EXPIRATION_TIME = ISSUE_AT + 3600000;
    private static final long REFRESH_TOKEN_EXPIRATION_TIME = ISSUE_AT + 80000000;

    private final Key key;
    public JwtProvider(String keyString){
        byte[] keyByte = Decoders.BASE64.decode(keyString);
        key = Keys.hmacShaKeyFor(keyByte);
    }

    public TokenResponseDto createToken(String claim){
        String accessToken = genAccessToken(claim);
        String refreshToken = genRefreshToken(claim);
        return TokenResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }
    private String genAccessToken(String claim){
        return Jwts.builder()
                    .setSubject("AccessToken")
                    .claim("user_email", claim)
                    .setIssuedAt(new Date(ISSUE_AT))
                    .setExpiration(new Date(ACCESS_TOKEN_EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }
    private String genRefreshToken(String claim){
        return Jwts.builder()
                    .setSubject("RefreshToken")
                    .claim("user_email", claim)
                    .setIssuedAt(new Date(ISSUE_AT))
                    .setExpiration(new Date(REFRESH_TOKEN_EXPIRATION_TIME))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
    }
}
