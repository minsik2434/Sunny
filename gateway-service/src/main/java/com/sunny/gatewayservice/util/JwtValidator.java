package com.sunny.gatewayservice.util;

import com.sunny.gatewayservice.exception.TokenValidateException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@Slf4j
public class JwtValidator {
    private final Key key;
    public JwtValidator(@Value("${jwt.secret}") String keyString){
        byte[] keyByte = Decoders.BASE64.decode(keyString);
        this.key = Keys.hmacShaKeyFor(keyByte);
    }

    public void validateToken(String accessToken){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
        } catch (ExpiredJwtException e) {
            throw new TokenValidateException("Expired AccessToken");
        } catch (UnsupportedJwtException e) {
            throw new TokenValidateException("Unsupported Token");
        } catch (MalformedJwtException e) {
            throw new TokenValidateException("Invalid Token");
        } catch (SignatureException e) {
            throw new TokenValidateException("SignatureException");
        } catch (IllegalArgumentException e) {
            throw new TokenValidateException("Jwt Claim is Empty");
        }
    }
}
