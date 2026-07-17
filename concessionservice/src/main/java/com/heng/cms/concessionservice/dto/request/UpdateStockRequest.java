package com.heng.cms.concessionservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UpdateStockRequest(
        @NotNull
    UUID id,
    @Min(0)
    Integer quantity
) {
    }