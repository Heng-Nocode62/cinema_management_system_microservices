package com.heng.cms.concessionservice.service;

import com.heng.cms.concessionservice.dto.request.MenuItemRequest;
import com.heng.cms.concessionservice.dto.response.MenuItemResponse;

import java.util.List;
import java.util.UUID;

public interface MenuItemService {
    MenuItemResponse createMenuItem(MenuItemRequest request);
    MenuItemResponse getMenuItem(UUID itemId);
    List<MenuItemResponse> getAllMenuItem();
    MenuItemResponse updateMenuItem(UUID itemId,MenuItemRequest request);
    void deleteMenuItem(UUID itemId);

}
