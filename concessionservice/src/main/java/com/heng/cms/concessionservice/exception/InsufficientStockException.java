package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(UUID stockRequestId) {
        super(
                "INSUFFICIENT_STOCK",
                "insufficient stock for inventory id="+stockRequestId,
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
