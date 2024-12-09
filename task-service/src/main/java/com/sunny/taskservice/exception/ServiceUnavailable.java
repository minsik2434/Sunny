package com.sunny.taskservice.exception;

public class ServiceUnavailable extends RuntimeException{
    public ServiceUnavailable(String message) {
        super(message);
    }

    public ServiceUnavailable(String message, Throwable cause) {
        super(message, cause);
    }
}
