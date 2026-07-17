package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends BusinessException{
    public BadRequestException(String message){
        super(
                "BAD_REQUEST",
                message,
                HttpStatus.BAD_REQUEST
        );
    }
}
