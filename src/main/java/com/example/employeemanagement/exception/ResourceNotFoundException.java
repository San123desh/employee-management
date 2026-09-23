package com.example.employeemanagement.exception;


/**
 * Thrown when a resource (employee, department, etc.) is not found.
 * The GlobalExceptionHandler will catch this and return HTTP 404.
 *
 * Why a custom exception (not just RuntimeException)?
 *   - Semantic clarity: reading the stack trace tells you EXACTLY what went wrong.
 *   - The exception handler can map it to a specific HTTP status (404)
 *     instead of a generic 500.
 */
public class ResourceNotFoundException extends RuntimeException{

    private final String resource;
    private final String field;
    private final Object value;

    public  ResourceNotFoundException(String resource, String field, Object value){
        super(String.format("%s not found with %s:%s", resource, field, value));
        this.resource = resource;
        this.field = field;
        this.value = value;
    }
    public String getResource() {return resource;}
    public String getField() {return field;}
    public Object getValue() {return value;}
}
