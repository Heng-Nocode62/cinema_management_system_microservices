package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicateComboItemException extends BusinessException{
    public DuplicateComboItemException() {
        super(
                "DUPLICATE_COMBO_ITEMS",
                "the provide combo items in combo update request is duplicated",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
