package com.sunny.userservice.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
public class FailResponse {
    private HttpStatus status;
    private String error;
    private String message;
    private String dateTime;
    private String path;
}
