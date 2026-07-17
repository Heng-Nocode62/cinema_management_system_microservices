package com.heng.cms.concessionservice.dto.response;

import com.heng.cms.concessionservice.domain.enumeric.Category;

import java.math.BigDecimal;
import java.util.UUID;

public record MenuItemResponse(
        UUID id,
        String name,
        String description,
        Category category,
        BigDecimal price,
        Integer calories,
        String imageUrl,
        boolean available) {
}
