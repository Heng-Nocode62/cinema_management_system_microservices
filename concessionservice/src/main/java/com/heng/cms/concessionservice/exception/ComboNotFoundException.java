package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class ComboNotFoundException extends BusinessException{
    public ComboNotFoundException() {
        super(
                "COMBO_NOT_FOUND",
                "combo with the provided id not found",
                HttpStatus.NOT_FOUND
        );

    }
}
