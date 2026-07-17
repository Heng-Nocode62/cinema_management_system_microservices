package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class InventoryNotFoundException extends BusinessException{
    public InventoryNotFoundException() {
        super(
                "INVENTORY_NOT_FOUND",
                "inventory with the provided id not found",
                HttpStatus.NOT_FOUND
        );
    }
}
