package com.project.order_management.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(OrderProcessingException.class)
    public ResponseEntity<Map<String, String>> handleOrderProcessingException(OrderProcessingException orderProcessingException) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", orderProcessingException.getMessage()));
    }
}
