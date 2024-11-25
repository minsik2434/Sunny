package com.sunny.gatewayservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Setter
@Getter
@Builder
public class FailResponse {
    private HttpStatus status;
    private String error;
    private String message;
    private String dateTime;
    private String path;
}
