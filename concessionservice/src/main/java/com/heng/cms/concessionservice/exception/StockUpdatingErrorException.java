package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class StockUpdatingErrorException extends BusinessException{
    public StockUpdatingErrorException() {
        super(
                "ERROR_UPDATING_STOCK",
                "cannot update stock because there is more than 1 stock with the provided id",
                HttpStatus.UNPROCESSABLE_ENTITY
        );
    }
}
