package com.sunny.projectservice.exception;

public class ServiceUnavailable extends RuntimeException{
    public ServiceUnavailable(String message) {
        super(message);
    }

    public ServiceUnavailable(String message, Throwable cause) {
        super(message, cause);
    }
}
