package com.heng.cms.movieservice.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }

}