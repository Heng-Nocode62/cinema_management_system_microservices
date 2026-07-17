package com.heng.cms.cinemaservice.exception;

public class BadRequestException extends RuntimeException{
    public BadRequestException(String message){
        super(message);
    }

}