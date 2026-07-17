package com.heng.cms.concessionservice.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ConcessionResponse(
        List<Item> items,
        BigDecimal totalPrice
) {
    public record Item(UUID id, String name, BigDecimal unitPrice, Integer quantity, ConcessionType type) {

    }
}
