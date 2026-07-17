package com.heng.cms.concessionservice.exception;

import org.springframework.http.HttpStatus;

public class MenuItemNotFoundException extends BusinessException {
    public MenuItemNotFoundException() {
        super(
                "MENU_ITEM_NOT_FOUND",
                "menu item with the privided id not found",
                HttpStatus.NOT_FOUND
        );
    }
}
