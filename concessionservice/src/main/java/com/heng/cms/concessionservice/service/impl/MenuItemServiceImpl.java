package com.heng.cms.concessionservice.service.impl;


import com.heng.cms.concessionservice.domain.entity.MenuItem;
import com.heng.cms.concessionservice.dto.request.MenuItemRequest;
import com.heng.cms.concessionservice.dto.response.MenuItemResponse;
import com.heng.cms.concessionservice.exception.BadRequestException;
import com.heng.cms.concessionservice.exception.MenuItemNotFoundException;
import com.heng.cms.concessionservice.mapper.MenuItemMapper;
import com.heng.cms.concessionservice.repository.MenuItemRepository;
import com.heng.cms.concessionservice.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl  implements MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final MenuItemMapper menuItemMapper;


    @Override
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        boolean existedByName = menuItemRepository.existsByNameAndAvailableTrue(request.name());
        if (existedByName) {
            throw new BadRequestException("menu item name already exists");
        }

        MenuItem menuItem = menuItemMapper.toMenuItem(request);

        menuItem.setAvailable(true);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);

        return  menuItemMapper.toMenuItemResponse(savedMenuItem);
    }

    @Override
    public MenuItemResponse getMenuItem(UUID itemId) {
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(MenuItemNotFoundException::new);
        return menuItemMapper.toMenuItemResponse(menuItem);
    }

    @Override
    public List<MenuItemResponse> getAllMenuItem() {
        List<MenuItem> menuItems = menuItemRepository.findByAvailableTrue();
        return menuItems.stream()
                .map(menuItemMapper::toMenuItemResponse)
                .toList();
    }

    // TODO consider updating partially
    @Override
    public MenuItemResponse updateMenuItem(UUID itemId, MenuItemRequest request) {
        boolean existedByName = menuItemRepository.existsByNameAndAvailableTrue(request.name());
        if (existedByName) {
            throw new BadRequestException("menu item name already exists");
        }
        MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(MenuItemNotFoundException::new);

        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setCategory(request.category());
        menuItem.setPrice(request.price());
        menuItem.setCalories(request.calories());
        menuItem.setImageUrl(request.imageUrl());
        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toMenuItemResponse(savedMenuItem);
    }

    @Override
    public void deleteMenuItem(UUID itemId) {
       MenuItem menuItem = menuItemRepository.findById(itemId)
                .orElseThrow(MenuItemNotFoundException::new);
       menuItem.setAvailable(false);
       menuItemRepository.save(menuItem);
    }
}
