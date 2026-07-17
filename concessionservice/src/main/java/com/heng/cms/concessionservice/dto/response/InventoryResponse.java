package com.heng.cms.concessionservice.dto.response;

import com.heng.cms.concessionservice.domain.enumeric.Category;

import java.util.UUID;

public record InventoryResponse(
        UUID id,
        Cinema cinema,
        MenuItem menuItem,
        Integer availableQuantity,
        Integer reserveQuantity

) {
    public record Cinema(UUID id,String name) {}
    public record MenuItem(UUID id, String name, Category category) {}
}
