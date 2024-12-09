package com.sunny.projectservice.common;

import com.sunny.projectservice.dto.FailResponse;
import com.sunny.projectservice.exception.PermissionException;
import com.sunny.projectservice.exception.DuplicateResourceException;
import com.sunny.projectservice.exception.ResourceNotFoundException;
import com.sunny.projectservice.exception.ServiceUnavailable;
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

    @ExceptionHandler(ServiceUnavailable.class)
    public ResponseEntity<FailResponse> serviceUnavailableExceptionResponse(ServiceUnavailable ex,
                                                                            HttpServletRequest request){
        FailResponse failResponse = createFailResponse(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable",
                ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(failResponse);
    }

    @ExceptionHandler(PermissionException.class)
    public ResponseEntity<FailResponse> lackOfPermissionExceptionResponse(PermissionException ex,
                                                                          HttpServletRequest request){
        FailResponse failResponse = createFailResponse(HttpStatus.UNAUTHORIZED,"UnAuthorization",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(failResponse);
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
