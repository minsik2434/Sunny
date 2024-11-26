package com.sunny.gatewayservice.exception;

public class TokenValidateException extends RuntimeException{
    public TokenValidateException(String message) {
        super(message);
    }
}
