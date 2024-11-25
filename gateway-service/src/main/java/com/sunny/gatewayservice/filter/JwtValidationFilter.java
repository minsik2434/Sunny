package com.sunny.gatewayservice.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.gatewayservice.dto.FailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class JwtValidationFilter extends AbstractGatewayFilterFactory<JwtValidationFilter.Config> {
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(!exchange.getRequest().getHeaders().containsKey("Authorization")){
                return validationFailResponse(exchange,HttpStatus.UNAUTHORIZED,
                        "Authorization header value not found");

            }
            String authorizationHeader = exchange.getRequest().getHeaders().get("Authorization").get(0);
            String token = authorizationHeader.replace("Bearer", "");


            return chain.filter(exchange);
        };
    }

    public JwtValidationFilter(){
        super(Config.class);
    }
    public static class Config{
    }

    private Mono<Void> validationFailResponse(ServerWebExchange exchange, HttpStatus status, String message)  {
        ObjectMapper mapper = new ObjectMapper();
        FailResponse failResponse = createFailResponse(status, exchange.getRequest().getPath().toString(), message);

        byte[] bytes;
        try {
            bytes = mapper.writeValueAsBytes(failResponse);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert Object to byte[]",e);
        }
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);
        exchange.getResponse().setStatusCode(status);
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }
    private FailResponse createFailResponse(HttpStatus status, String path, String message){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return FailResponse.builder()
                .status(status)
                .error("validation failed")
                .message(message)
                .dateTime(dateFormat.format(new Date(System.currentTimeMillis())))
                .path(path).build();
    }
}
