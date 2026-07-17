package com.heng.cms.concessionservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException{
    private String errorCode;
    private HttpStatus httpStatus;

    public BusinessException( String errorCode,String message, HttpStatus httpStatus){
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

}
