package com.sunny.taskservice.common;

import com.sunny.taskservice.dto.FailResponse;
import com.sunny.taskservice.exception.DuplicateResourceException;
import com.sunny.taskservice.exception.ResourceNotFoundException;
import com.sunny.taskservice.exception.ServiceUnavailable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailResponse> validationExceptionResponse(MethodArgumentNotValidException ex,
                                                                    HttpServletRequest request){
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.put(fieldError.getField(), fieldError.getDefaultMessage())
        );

        ex.getBindingResult().getGlobalErrors().forEach(globalError ->
                errors.put(globalError.getObjectName(), globalError.getDefaultMessage())
        );

        FailResponse validationError = createFailResponse(HttpStatus.BAD_REQUEST,
                "Validation Error", errors.toString(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<FailResponse> resourceNotFoundExceptionResponse(ResourceNotFoundException ex,
                                                                          HttpServletRequest request){
        FailResponse notFound = createFailResponse(HttpStatus.NOT_FOUND, "Not Found",
                ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFound);
    }

    @ExceptionHandler(ServiceUnavailable.class)
    public ResponseEntity<FailResponse> serviceUnavailableExceptionResponse(ServiceUnavailable ex,
                                                                            HttpServletRequest request){
        FailResponse serviceUnavailable = createFailResponse(HttpStatus.SERVICE_UNAVAILABLE,
                "Service Unavailable", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(serviceUnavailable);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<FailResponse> duplicateExceptionResponse(DuplicateResourceException ex,
                                                                   HttpServletRequest request){
        FailResponse failResponse = createFailResponse(HttpStatus.CONFLICT,
                "Resource Duplicate", ex.getMessage(), request.getRequestURI());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(failResponse);
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
