package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicateComboRequestException extends BusinessException{
    public DuplicateComboRequestException() {
        super(
                "DUPLICATE_COMBO_REQUEST",
                "some of the provided combo ids are duplicated",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
