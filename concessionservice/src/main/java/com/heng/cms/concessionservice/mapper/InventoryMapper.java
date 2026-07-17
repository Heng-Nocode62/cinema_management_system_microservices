package com.heng.cms.concessionservice.mapper;

import com.heng.cms.concessionservice.domain.entity.Inventory;
import com.heng.cms.concessionservice.domain.entity.MenuItem;
import com.heng.cms.concessionservice.dto.request.CreateInventoryRequest;
import com.heng.cms.concessionservice.dto.response.InventoryResponse;
import com.heng.cms.concessionservice.exception.MenuItemNotFoundException;
import com.heng.cms.concessionservice.repository.MenuItemRepository;
import com.heng.cms.concessionservice.service.client.CinemaClient;
import com.heng.cms.concessionservice.service.client.dto.CinemaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InventoryMapper {
    private final MenuItemRepository menuItemRepository;
    private final CinemaClient cinemaClient;

    public Inventory toInventory(CreateInventoryRequest request) {
        return Inventory.builder()
                .cinemaId(request.cinemaId())
                .menuItemId(request.menuItemId())
                .availableQuantity(request.quantity())
                .build();
    }
    public InventoryResponse toInventoryResponse(Inventory inventory, CinemaResponse cinema, MenuItem menuItem) {
        return new InventoryResponse(
                inventory.getId(),
                new InventoryResponse.Cinema(cinema.getId(),cinema.getName()),
                new InventoryResponse.MenuItem(menuItem.getId(),menuItem.getName(),menuItem.getCategory()),
                inventory.getAvailableQuantity(),
                inventory.getReserveQuantity()
        );
    }

    public InventoryResponse toInventoryResponse(Inventory inventory) {
        CinemaResponse cinema = cinemaClient.getCinemaById(inventory.getCinemaId());
        MenuItem menuItem = menuItemRepository.findById(inventory.getMenuItemId())
                .orElseThrow(MenuItemNotFoundException::new);
        return new InventoryResponse(
                inventory.getId(),
                new InventoryResponse.Cinema(cinema.getId(),cinema.getName()),
                new InventoryResponse.MenuItem(menuItem.getId(),menuItem.getName(),menuItem.getCategory()),
                inventory.getAvailableQuantity(),
                inventory.getReserveQuantity()
        );
    }
}
