package com.sunny.taskservice.common;

import com.sunny.taskservice.dto.FailResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.SimpleDateFormat;

@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailResponse> validationExceptionResponse(){
        return null;
    }
    private FailResponse createFailResponse(HttpStatus status, String error, String message, String requestUrl){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return FailResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .dateTime(dateFormat.format(System.currentTimeMillis()))
                .path(requestUrl).build();
    }
}
