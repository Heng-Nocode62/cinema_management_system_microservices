package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class InventoryAlreadyExistException extends BusinessException {
    public InventoryAlreadyExistException() {
        super(
                "INVENTORY_ALREADY_EXIST",
                "inventory already exist for the provided cinema id and menu id",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
