package com.sunny.userservice.common.exception;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
