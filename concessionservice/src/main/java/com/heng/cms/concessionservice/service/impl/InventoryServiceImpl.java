package com.heng.cms.concessionservice.service.impl;

import com.heng.cms.concessionservice.domain.entity.Inventory;
import com.heng.cms.concessionservice.domain.entity.MenuItem;
import com.heng.cms.concessionservice.dto.request.CreateInventoryRequest;
import com.heng.cms.concessionservice.dto.request.UpdateStockRequest;
import com.heng.cms.concessionservice.dto.response.InventoryResponse;
import com.heng.cms.concessionservice.event.ConcessionEventPublisher;
import com.heng.cms.concessionservice.exception.*;
import com.heng.cms.concessionservice.mapper.InventoryMapper;
import com.heng.cms.concessionservice.repository.InventoryRepository;
import com.heng.cms.concessionservice.repository.MenuItemRepository;
import com.heng.cms.concessionservice.service.client.CinemaClient;
import com.heng.cms.concessionservice.service.InventoryService;
import com.heng.cms.concessionservice.service.client.dto.CinemaResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository inventoryRepository;
    private final MenuItemRepository menuItemRepository;
    private final CinemaClient cinemaClient;
    private final InventoryMapper inventoryMapper;
    private final ConcessionEventPublisher eventPublisher;

    @Override
    @Transactional
    public InventoryResponse createInventory(CreateInventoryRequest request) {

        boolean existed = inventoryRepository.existsByCinemaIdAndMenuItemId(request.cinemaId(),request.menuItemId());

        if (existed) {
            log.warn("Inventory already exist for cinemaId={} and menuItemId {}", request.cinemaId(),request.menuItemId());
            throw new InventoryAlreadyExistException();
        }

        CinemaResponse cinema = cinemaClient.getCinemaById(request.cinemaId());
        MenuItem menuItem = menuItemRepository.findById(request.menuItemId())
                .orElseThrow(MenuItemNotFoundException::new);
        Inventory inventory = inventoryMapper.toInventory(request);
        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toInventoryResponse(savedInventory,cinema,menuItem);
    }

    @Override
    public InventoryResponse getInventoryById(UUID inventoryId) {
        Inventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(InventoryNotFoundException::new);
        return inventoryMapper.toInventoryResponse(inventory);
    }

    @Override
    public InventoryResponse getInventoryByCinemaIdAndMenuItemId(UUID cinemaId, UUID menuItemId) {
       Inventory inventory= inventoryRepository.findByCinemaIdAndMenuItemId(cinemaId, menuItemId)
                .orElseThrow(InventoryNotFoundException::new);
        return inventoryMapper.toInventoryResponse(inventory);
    }

    // TODO add filter with specification, this request contain many request
    @Override
    public List<InventoryResponse> getAllInventory(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
        List<Inventory> inventories =  inventoryPage.getContent();
        List<UUID> cinemaIds = inventories.stream().map(Inventory::getCinemaId).toList();
        List<UUID> menuItemIds = inventories.stream().map(Inventory::getMenuItemId).toList();
        Map<UUID, MenuItem> menuItems = menuItemRepository
                .findAllById(menuItemIds)
                .stream()
                .collect(Collectors.toMap(MenuItem::getId, Function.identity()));
        Map<UUID, CinemaResponse> cinemas = cinemaClient.getCinemaByIds(cinemaIds).stream()
                .collect(Collectors.toMap(CinemaResponse::getId, Function.identity()));

       return inventories.stream().map(inventory -> {
            CinemaResponse cinema = cinemas.get(inventory.getCinemaId());
            MenuItem menuItem = menuItems.get(inventory.getMenuItemId());
            if (cinema != null && menuItem != null) {
                return inventoryMapper.toInventoryResponse(inventory, cinema, menuItem);
            }
            return inventoryMapper.toInventoryResponse(inventory);
        }).toList();


    }

    @Override
    public List<InventoryResponse> getAllInventory(Map<String, Object> filterParams) {

        return List.of();
    }

    @Override
    public void updateStock(UpdateStockRequest request) {
        Inventory inventory = inventoryRepository.findById(request.id())
                .orElseThrow(InventoryNotFoundException::new);
        inventory.setAvailableQuantity(request.quantity());
        inventoryRepository.save(inventory);
    }

    @Transactional
    @Override
    public void increaseStock(UpdateStockRequest request) {
        Inventory inventory = inventoryRepository.findById(request.id())
                .orElseThrow(InventoryNotFoundException::new);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity()+request.quantity());
    }


    @Transactional
    @Override
    public void decreaseStocks(List<UpdateStockRequest> requests) {
        List<UUID> inventoryIds = requests.stream().map(UpdateStockRequest::id).toList();
        if (inventoryIds.size() != Set.copyOf(inventoryIds).size()) {
            throw new DuplicateUpdateStockRequest();
        }
        List<Inventory> inventories = inventoryRepository.findAllByIdForUpdate(inventoryIds);
        if (inventories.size() != inventoryIds.size()) {
            throw new InventoryNotFoundException();
        }
        Map<UUID,Inventory> inventoryMap = inventories.stream().collect(Collectors.toMap(Inventory::getId, Function.identity()));

        for (UpdateStockRequest stockRequest : requests) {
            Inventory inventory = inventoryMap.get(stockRequest.id());
            if(inventory.getAvailableQuantity() < stockRequest.quantity()) {
                log.warn("Inventory availableQuantity less than stock request availableQuantity inventoryId={}",inventory.getId());
                throw new InsufficientStockException(inventory.getId());
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - stockRequest.quantity());

            if(inventory.getAvailableQuantity() <= inventory.getReorderThreshold()) {
                log.warn("Low stock: menuItemId={} cinemaId={} qty={}", inventory.getMenuItemId(), inventory.getCinemaId(), inventory.getAvailableQuantity());
                eventPublisher.publishLowInventory(inventory.getMenuItemId(), inventory.getCinemaId(), inventory.getAvailableQuantity());
            }

        }
    }

}
