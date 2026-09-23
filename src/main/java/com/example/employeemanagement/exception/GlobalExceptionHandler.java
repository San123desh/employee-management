package com.example.employeemanagement.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * @RestControllerAdvice = "this class handles exceptions for ALL controllers.
 * Builds A JSON error body(status code, message, timestamp)"
 *
 * Without this:
 *   - ResourceNotFoundException → Spring returns a raw 500 with a stack trace.
 *   - Ugly, leaks internal info, and the client can't tell "not found" from "server error."
 *
 * With this:
 *   - ResourceNotFoundException → clean JSON: { "status": 404, "message": "Employee not found with id: 99" }
 */


@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex){
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Bad input from client (bad ID format, validation)
    @ExceptionHandler(IllegalArgumentException.class)
    public  ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex){
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", ex.getMessage());
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // (NPE, DB errors, etc.) is a server fault 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(Exception ex){
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("message", "An unexpected error occurred");
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Catches validation failures from @Valid.
     * Returns 400 with the specific field errors:
     *   { "status": 400, "message": "Validation failed",
     *     "errors": { "email": "Email must be a valid email address",
     *                 "salary": "Salary must be greater than zero" } }
     */

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        // ex.getBindingResult() → ex.getBindingResult() gives you access to that collected list of failures:
        //   .getFieldErrors()   → list of all failed field validations
        //   .forEach(...)       → put each one into the map
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fieldErrors.put(error.getField(), error.getDefaultMessage())
        );

        // full json response body
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("message", "Validation failed");
        body.put("errors", fieldErrors); //neested map goes here
        body.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.badRequest().body(body);
    }
}
