package com.personal.uber_video.exception;

import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;

@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException{
    private final HttpStatus statusCode;
    
    public ApiException(String message, HttpStatus statusCode){
        super(message);
        this.statusCode = statusCode;
    }
    
    public HttpStatus getStatusCode() {
        return statusCode;
    }
}
