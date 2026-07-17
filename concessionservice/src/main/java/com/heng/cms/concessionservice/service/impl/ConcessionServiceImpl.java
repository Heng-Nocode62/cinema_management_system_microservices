package com.heng.cms.concessionservice.service.impl;

import com.heng.cms.concessionservice.domain.entity.Combo;
import com.heng.cms.concessionservice.domain.entity.ComboItem;
import com.heng.cms.concessionservice.domain.entity.Inventory;
import com.heng.cms.concessionservice.domain.entity.MenuItem;
import com.heng.cms.concessionservice.dto.*;
import com.heng.cms.concessionservice.event.ConcessionEventPublisher;
import com.heng.cms.concessionservice.exception.*;
import com.heng.cms.concessionservice.repository.ComboRepository;
import com.heng.cms.concessionservice.repository.InventoryRepository;
import com.heng.cms.concessionservice.repository.MenuItemRepository;
import com.heng.cms.concessionservice.service.ConcessionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;



/*
    TODO refactor code to reusable function, because i repeated validation in each method
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class ConcessionServiceImpl implements ConcessionService {
    private final ComboRepository comboRepository;
    private final MenuItemRepository menuItemRepository;
    private final InventoryRepository inventoryRepository;
    private final ConcessionEventPublisher eventPublisher;
    @Override

    @Transactional
    public ConcessionResponse reserveConcession(ReserveConcessionRequest request) {

        //  validate
        List<UUID> comboIds = request.combos().stream().map(ReserveConcessionRequest.Item::id).toList();

        if (comboIds.size() != Set.copyOf(comboIds).size()) {
            throw new DuplicateComboRequestException();
        }

        List<UUID> menuItemIds = request.items().stream().map(ReserveConcessionRequest.Item::id).toList();
        if (menuItemIds.size() != Set.copyOf(menuItemIds).size()) {
            throw new DuplicateMenuItemRequestException();
        }

        List<Combo> combos = comboRepository.findAllByIdInAndActiveTrue(comboIds);
        if (combos.size() != comboIds.size()) {
            throw new ComboNotFoundException();
        }
        List<MenuItem> menuItems = menuItemRepository.findAllByIdInAndAvailableTrue(menuItemIds);

        if (menuItems.size() != menuItemIds.size()) {
            throw new MenuItemNotFoundException();
        }

        // map from combos to map of menuItem id and amount

        Map<UUID, Combo> comboMap = combos.stream()
                .collect(Collectors.toMap(Combo::getId, Function.identity()));

        Map<UUID, MenuItem> menuItemMap = menuItems.stream().collect(Collectors.toMap(MenuItem::getId, Function.identity()));
        Map<UUID, Integer> requiredMenuItem= new HashMap<>();

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (ReserveConcessionRequest.Item comboRequest : request.combos()) {
            Combo combo = comboMap.get(comboRequest.id());
            List<ComboItem> comboItems = combo.getComboItems();
            totalPrice = totalPrice.add(combo.getPrice().multiply(new BigDecimal(comboRequest.quantity())));
            for (ComboItem comboItem : comboItems) {
                requiredMenuItem.merge(
                        comboItem.getMenuItem().getId(),
                        comboItem.getQuantity()*comboRequest.quantity(),
                        Integer::sum
                );
            }

        }
        for (ReserveConcessionRequest.Item itemRequest : request.items()) {
            MenuItem menuItem = menuItemMap.get(itemRequest.id());
            totalPrice = totalPrice.add(menuItem.getPrice().multiply(new BigDecimal(itemRequest.quantity())));
            requiredMenuItem.merge(
                    itemRequest.id(),
                    itemRequest.quantity(),
                    Integer::sum
            );
        }
        List<Inventory> inventories = inventoryRepository.findAllByCinemaIdAndMenuItemIdForUpdate(request.cinemaId(),requiredMenuItem.keySet());
        Map<UUID, Inventory> inventoryMap = inventories.stream().collect(Collectors.toMap(Inventory::getMenuItemId, Function.identity()));
        for (Map.Entry<UUID, Integer> requiredItem : requiredMenuItem.entrySet()) {
            Inventory inventory = inventoryMap.get(requiredItem.getKey());
            if (inventory.getAvailableQuantity() < requiredItem.getValue()) {
                log.warn("Inventory availableQuantity less than request availableQuantity inventoryId={}",inventory.getId());
                throw new InsufficientStockException(inventory.getId());
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - requiredItem.getValue());
            inventory.setReserveQuantity(inventory.getReserveQuantity() + requiredItem.getValue());
            if (inventory.getAvailableQuantity() < inventory.getReorderThreshold()) {
                log.warn("Low stock: menuItemId={} cinemaId={} qty={}", inventory.getMenuItemId(), inventory.getCinemaId(), inventory.getAvailableQuantity());
                eventPublisher.publishLowInventory(inventory.getMenuItemId(), inventory.getCinemaId(), inventory.getAvailableQuantity());
            }
        }

        List<ConcessionResponse.Item> itemResponses = new ArrayList<>();
        for (ReserveConcessionRequest.Item itemRequest : request.items()) {
            MenuItem menuItem = menuItemMap.get(itemRequest.id());
            itemResponses.add(new ConcessionResponse.Item(
                    menuItem.getId(),
                    menuItem.getName(),
                    menuItem.getPrice(),
                    itemRequest.quantity(),
                    ConcessionType.ITEM
            ));
        }

        for (ReserveConcessionRequest.Item comboRequest : request.combos()) {
            Combo combo = comboMap.get(comboRequest.id());
            itemResponses.add(new ConcessionResponse.Item(
                    combo.getId(),
                    combo.getName(),
                    combo.getPrice(),
                    comboRequest.quantity(),
                    ConcessionType.COMBO
            ));
        }
        return new ConcessionResponse(
                itemResponses,
                totalPrice
        );


    }


    @Transactional
    @Override
    public void confirmReservedItem(ConcessionRequest request){
        Map<ConcessionType, List<ConcessionRequest.Item>> requestMap = request.items().stream()
                .collect(Collectors.groupingBy(ConcessionRequest.Item::type));

        Map<UUID,Integer> itemRequest = new HashMap<>();
        if (requestMap.get(ConcessionType.ITEM)!= null){
            itemRequest = requestMap.get(ConcessionType.ITEM).stream()
                    .collect(Collectors.toMap(ConcessionRequest.Item::id, ConcessionRequest.Item::quantity));
        }


        Map<UUID,Integer> comboRequest = new HashMap<>();
        if (requestMap.get(ConcessionType.COMBO)!= null){
            comboRequest = requestMap.get(ConcessionType.COMBO).stream()
                    .collect(Collectors.toMap(ConcessionRequest.Item::id, ConcessionRequest.Item::quantity));
        }



        List<Combo> combos = comboRepository.findAllByIdInAndActiveTrue(comboRequest.keySet());
        if (combos.size() != comboRequest.size()) {
            throw new ComboNotFoundException();
        }

        List<MenuItem> menuItems = menuItemRepository.findAllByIdInAndAvailableTrue(itemRequest.keySet());
        if (menuItems.size() != itemRequest.size()) {
            throw new MenuItemNotFoundException();
        }
        Map<UUID,Combo> comboMap = combos.stream().collect(Collectors.toMap(Combo::getId, Function.identity()));
        Map<UUID,MenuItem> menuItemMap = menuItems.stream().collect(Collectors.toMap(MenuItem::getId, Function.identity()));



        Map<UUID,Integer> requiredItem = new HashMap<>(Map.copyOf(itemRequest));
        for (Map.Entry<UUID,Integer> comboRequestItem : comboRequest.entrySet()) {
            Combo combo = comboMap.get(comboRequestItem.getKey());
            List<ComboItem> comboItems = combo.getComboItems();
            for (ComboItem comboItem : comboItems) {
                requiredItem.merge(
                        comboItem.getMenuItem().getId(),
                        comboRequestItem.getValue()*comboItem.getQuantity(),
                        Integer::sum
                );
            }
        }
        List<Inventory> inventories = inventoryRepository.findAllByCinemaIdAndMenuItemIdForUpdate(request.cinemaId(),requiredItem.keySet());
        if (inventories.size() != requiredItem.size()) {
            throw new InventoryNotFoundException();
        }

        Map<UUID, Inventory> inventoryMap = inventories.stream().collect(Collectors.toMap(Inventory::getMenuItemId, Function.identity()));

        // TODO check this method again, too dangerous to use
        for (Map.Entry<UUID, Integer> item : requiredItem.entrySet()) {
            Inventory inventory = inventoryMap.get(item.getKey());
            if (inventory.getReserveQuantity() < item.getValue()){
                log.error("Inventory reserve availableQuantity less than confirm request availableQuantity inventoryId={}",inventory.getId());
                throw new  InsufficientStockException(inventory.getId());
            }
            inventory.setReserveQuantity(inventory.getReserveQuantity() - item.getValue());
        }


    }


    @Transactional
    @Override
    public void cancelReservedItem(ConcessionRequest request){
        Map<ConcessionType, List<ConcessionRequest.Item>> requestMap = request.items().stream()
                .collect(Collectors.groupingBy(ConcessionRequest.Item::type));

        Map<UUID,Integer> itemRequest = new HashMap<>();
        if (requestMap.get(ConcessionType.ITEM)!= null){
           itemRequest = requestMap.get(ConcessionType.ITEM).stream()
                    .collect(Collectors.toMap(ConcessionRequest.Item::id, ConcessionRequest.Item::quantity));
        }


        Map<UUID,Integer> comboRequest = new HashMap<>();
        if (requestMap.get(ConcessionType.COMBO)!= null){
            comboRequest = requestMap.get(ConcessionType.COMBO).stream()
                    .collect(Collectors.toMap(ConcessionRequest.Item::id, ConcessionRequest.Item::quantity));
        }

        List<Combo> combos = comboRepository.findAllByIdInAndActiveTrue(comboRequest.keySet());
        if (combos.size() != comboRequest.size()) {
            throw new ComboNotFoundException();
        }

        List<MenuItem> menuItems = menuItemRepository.findAllByIdInAndAvailableTrue(itemRequest.keySet());
        if (menuItems.size() != itemRequest.size()) {
            throw new MenuItemNotFoundException();
        }
        Map<UUID,Combo> comboMap = combos.stream().collect(Collectors.toMap(Combo::getId, Function.identity()));

        Map<UUID,Integer> requiredItem = new HashMap<>(Map.copyOf(itemRequest));

        requiredItem.putAll(itemRequest);
        for (Map.Entry<UUID,Integer> comboRequestItem : comboRequest.entrySet()) {
            Combo combo = comboMap.get(comboRequestItem.getKey());
            List<ComboItem> comboItems = combo.getComboItems();
            for (ComboItem comboItem : comboItems) {
                requiredItem.merge(
                        comboItem.getMenuItem().getId(),
                        comboRequestItem.getValue()*comboItem.getQuantity(),
                        Integer::sum
                );
            }
        }
        List<Inventory> inventories = inventoryRepository.findAllByCinemaIdAndMenuItemIdForUpdate(request.cinemaId(),requiredItem.keySet());
        if (inventories.size() != requiredItem.size()) {
            throw new InventoryNotFoundException();
        }

        Map<UUID, Inventory> inventoryMap = inventories.stream().collect(Collectors.toMap(Inventory::getMenuItemId, Function.identity()));

        for (Map.Entry<UUID, Integer> item : requiredItem.entrySet()) {
            Inventory inventory = inventoryMap.get(item.getKey());
            inventory.setReserveQuantity(inventory.getAvailableQuantity() - item.getValue());
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() +item.getValue());
        }
    }

}
