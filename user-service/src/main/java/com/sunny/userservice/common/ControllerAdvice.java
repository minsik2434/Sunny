package com.sunny.userservice.common;

import com.sunny.userservice.common.exception.DuplicateResourceException;
import com.sunny.userservice.common.exception.ResourceNotFoundException;
import com.sunny.userservice.dto.FailResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.SimpleDateFormat;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<FailResponse> duplicateExceptionResponse(DuplicateResourceException ex,
                                                                   HttpServletRequest request){
        FailResponse failResponse = createFailResponse(HttpStatus.CONFLICT, "Duplicate",
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(failResponse);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<FailResponse> notFoundExceptionResponse(ResourceNotFoundException ex,
                                                                  HttpServletRequest request){
        FailResponse failResponse = createFailResponse(HttpStatus.NOT_FOUND, "Not Found",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(failResponse);
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
