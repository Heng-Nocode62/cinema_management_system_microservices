package com.heng.cms.concessionservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateComboRequest (
        @NotBlank
        String name,
        @NotNull
        @Min(0)
        BigDecimal price,
        @NotNull
        @NotEmpty
        List<Item> items
){
    public record Item (UUID menuItemId, Integer quantity){}
}
