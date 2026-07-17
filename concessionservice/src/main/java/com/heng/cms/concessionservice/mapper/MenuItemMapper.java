package com.heng.cms.concessionservice.mapper;

import com.heng.cms.concessionservice.domain.entity.MenuItem;
import com.heng.cms.concessionservice.dto.request.MenuItemRequest;
import com.heng.cms.concessionservice.dto.response.MenuItemResponse;
import org.springframework.stereotype.Component;

@Component
public class MenuItemMapper {

    public MenuItem toMenuItem(MenuItemRequest request){
        return MenuItem.builder()
                .name(request.name())
                .description(request.description())
                .category(request.category())
                .price(request.price())
                .calories(request.calories())
                .imageUrl(request.imageUrl())
                .build();
    }
    public MenuItemResponse toMenuItemResponse(MenuItem menuItem){
        return new MenuItemResponse(
                menuItem.getId(),
                menuItem.getName(),
                menuItem.getDescription(),
                menuItem.getCategory(),
                menuItem.getPrice(),
                menuItem.getCalories(),
                menuItem.getImageUrl(),
                menuItem.isAvailable()
        );
    }
}
