package com.heng.cms.concessionservice.service.impl;

import com.heng.cms.concessionservice.domain.entity.Combo;
import com.heng.cms.concessionservice.domain.entity.ComboItem;
import com.heng.cms.concessionservice.domain.entity.MenuItem;
import com.heng.cms.concessionservice.dto.request.CreateComboRequest;
import com.heng.cms.concessionservice.dto.request.UpdateComboRequest;
import com.heng.cms.concessionservice.dto.response.ComboResponse;
import com.heng.cms.concessionservice.exception.*;
import com.heng.cms.concessionservice.mapper.ComboMapper;
import com.heng.cms.concessionservice.repository.ComboItemRepository;
import com.heng.cms.concessionservice.repository.ComboRepository;
import com.heng.cms.concessionservice.repository.MenuItemRepository;
import com.heng.cms.concessionservice.service.ComboService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ComboServiceImpl implements ComboService {
    private final ComboRepository comboRepository;
    private final MenuItemRepository menuItemRepository;
    private final ComboItemRepository  comboItemRepository;
    private final ComboMapper comboMapper;

    @Override
    @Transactional
    public ComboResponse createCombo(CreateComboRequest request) {
        boolean existedByName = comboRepository.existsByNameAndActiveTrue(request.name());
        if (existedByName) {
            throw new BadRequestException("Combo with name " + request.name() + " already exists");
        }

        List<UUID> itemIds = request.items().stream().map(CreateComboRequest.Item::menuItemId).toList();
        if(itemIds.isEmpty()){
            throw new BadRequestException("menuItem is empty");
        }
        if (itemIds.size()!=request.items().size()){
            throw new BadRequestException("Duplicate menuItemId");
        }
        List<MenuItem> menuItems = menuItemRepository.findAllById(itemIds);
        Map<UUID, MenuItem> menuItemMap = menuItems.stream().collect(Collectors.toMap(MenuItem::getId, Function.identity()));

        if(itemIds.size()!=menuItems.size()){
            log.warn("Some menuItemIds were not found");
            throw new MenuItemNotFoundException();
        }
        Combo combo = Combo.builder()
                .name(request.name())
                .price(request.price())
                .active(true)
                .build();
        Combo savedCombo = comboRepository.save(combo);

        List<ComboItem> comboItems = new ArrayList<>();
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CreateComboRequest.Item item : request.items()) {
            MenuItem menuItem = menuItemMap.get(item.menuItemId());
            BigDecimal pricePerMenuItem = menuItem.getPrice().multiply(new BigDecimal(item.quantity()));
            totalPrice = totalPrice.add(pricePerMenuItem);
            ComboItem comboItem = ComboItem.builder()
                    .combo(savedCombo)
                    .menuItem(menuItem)
                    .quantity(item.quantity())
                    .build();
            comboItems.add(comboItem);
        }
        comboItemRepository.saveAll(comboItems);
        BigDecimal savingAmount = totalPrice.subtract(request.price());
        savedCombo.setSavingsAmount(savingAmount);

        return comboMapper.toComboResponse(savedCombo,comboItems);
    }

    @Override
    public ComboResponse getComboById(UUID comboId) {

        Combo combo = comboRepository.findByIdAndActiveTrue(comboId)
                .orElseThrow(ComboNotFoundException::new);

        return comboMapper.toComboResponse(combo,combo.getComboItems());
    }

    @Override
    public List<ComboResponse> getAllCombos() {
        List<Combo> combos = comboRepository.findAllByActiveTrue();
        return combos.stream()
                .map(combo -> comboMapper.toComboResponse(combo,combo.getComboItems()))
                .toList();
    }

    @Override
    @Transactional
    public ComboResponse updateCombo(UUID comboId, UpdateComboRequest request) {
        boolean existedByName = comboRepository.existsByNameAndActiveTrue(request.name());
        if (existedByName) {
            throw new BadRequestException("Combo with name " + request.name() + " already exists");
        }
        Combo combo = comboRepository.findByIdAndActiveTrue(comboId).orElseThrow(ComboNotFoundException::new);
        combo.setName(request.name());
        List<UUID> comboItemIds = request.items().stream().map(UpdateComboRequest.Item::id).toList();

        if (Set.copyOf(comboItemIds).size()!=request.items().size()){
            throw new DuplicateComboItemException();
        }

        List<ComboItem> comboItems = comboItemRepository.findAllById(comboItemIds);
        if (comboItems.size()!=comboItemIds.size()){
            throw new ComboItemNotFoundException();
        }
        Map<UUID,ComboItem> comboItemMap = comboItems.stream().collect(Collectors.toMap(ComboItem::getId, Function.identity()));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (UpdateComboRequest.Item item : request.items()) {
            ComboItem comboItem = comboItemMap.get(item.id());
            comboItem.setQuantity(item.quantity());
            BigDecimal comboItemPrice = comboItem.getCombo().getPrice().multiply(new BigDecimal(item.quantity()));
            totalPrice = totalPrice.add(comboItemPrice);

        }
        BigDecimal savingAmount = totalPrice.subtract(request.price());
        combo.setPrice(request.price());

        combo.setSavingsAmount(savingAmount);

        return  comboMapper.toComboResponse(combo,comboItems);

    }

    @Override
    @Transactional
    public void deleteCombo(UUID comboId) {
        Combo combo = comboRepository.findByIdAndActiveTrue(comboId).orElseThrow(ComboNotFoundException::new);
        combo.setActive(false);
    }
}
