package com.personal.uber_video.exception;

public class ApiException extends RuntimeException{
    public ApiException(String message){
        super(message);
    }
}
