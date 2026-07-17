package com.heng.cms.concessionservice.dto;

import com.heng.cms.concessionservice.exception.BusinessException;
import org.springframework.http.HttpStatus;

public class DuplicateMenuItemRequestException extends BusinessException {
    public DuplicateMenuItemRequestException() {
        super(
                "DUPLICATE_MENU_ITEM_IN_REQUEST",
                "some of the provided menuItem id are duplicated",
                HttpStatus.UNPROCESSABLE_ENTITY
                );
    }
}
