package com.personal.uber_video.exception;


import com.personal.uber_video.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<?> handleApiException(ApiException e) {
        String message = e.getMessage();
        ApiResponse apiResponse = new ApiResponse(message);
        return new ResponseEntity<>(apiResponse, e.getStatusCode());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public  ResponseEntity<?> handleUsernameNotFoundException(UsernameNotFoundException e) {
        String message = e.getMessage();
        ApiResponse apiResponse = new ApiResponse(message);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

}
