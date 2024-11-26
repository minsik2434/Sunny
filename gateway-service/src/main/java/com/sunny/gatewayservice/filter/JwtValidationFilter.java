package com.sunny.gatewayservice.filter;

import com.sunny.gatewayservice.exception.TokenValidateException;
import com.sunny.gatewayservice.util.JwtValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {
    private final JwtValidator jwtValidator;
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey("Authorization")){
                throw new TokenValidateException("Authorization header value not found");
            }
            String authorizationHeader = exchange.getRequest().getHeaders().get("Authorization").get(0);
            String accessToken = authorizationHeader.replace("Bearer", "");
            jwtValidator.validateToken(accessToken);
            return chain.filter(exchange);
        };
    }

    public JwtValidationFilter(JwtValidator jwtValidator){
        super(Config.class);
        this.jwtValidator = jwtValidator;
    }
    public static class Config{
    }
}
