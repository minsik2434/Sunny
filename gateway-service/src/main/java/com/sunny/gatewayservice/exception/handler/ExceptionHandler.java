package com.sunny.gatewayservice.exception.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunny.gatewayservice.dto.FailResponse;
import com.sunny.gatewayservice.exception.TokenValidateException;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;

@Order(-1)
@Component
public class ExceptionHandler implements ErrorWebExceptionHandler {
    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();
        Mono<Void> mono = null;
        if(response.isCommitted()){
            mono = Mono.error(ex);
        }
        if(ex instanceof TokenValidateException){
            mono = failResponse(exchange, HttpStatus.UNAUTHORIZED, ex.getMessage());
        }

        return mono;
    }

    private Mono<Void> failResponse(ServerWebExchange exchange, HttpStatus status, String message)  {
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
