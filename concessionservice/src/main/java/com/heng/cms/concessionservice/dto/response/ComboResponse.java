package com.heng.cms.concessionservice.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ComboResponse (
        UUID id,
        String name,
        BigDecimal price,
        BigDecimal savingAmount,
        List<ItemResponse> comboItems
){
    public record ItemResponse(UUID id,
                               UUID menuItemId,
                               Integer quantity){}
}
