package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class ComboItemNotFoundException extends BusinessException {
    public ComboItemNotFoundException() {
        super(
                "COMBO_ITEM_NOT_FOUND",
                "cannot find all the combo items from the provided ids",
                HttpStatus.NOT_FOUND
        );
    }
}
