package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class CinemaFindingException extends BusinessException {
    public CinemaFindingException(String message) {
        super(
                "ERROR_FINDING_CINEMA",
                message,
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
