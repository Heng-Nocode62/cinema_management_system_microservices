package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class DuplicateUpdateStockRequest extends BusinessException {
    public DuplicateUpdateStockRequest() {
        super(
                "INVENTORY_DUPLICATED",
                "some of the provided inventoryIds are duplicated",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
