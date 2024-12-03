package com.sunny.projectservice.exception;

public class DuplicateResourceException extends RuntimeException{
    public DuplicateResourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateResourceException(String message) {
        super(message);
    }
}
