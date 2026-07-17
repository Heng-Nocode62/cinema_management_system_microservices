package com.heng.cms.concessionservice.dto.request;

import com.heng.cms.concessionservice.domain.enumeric.Category;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record MenuItemRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        Category category, // snack | drink | meal | combo

        @NotNull
        @DecimalMin("0.01")
        BigDecimal price,

        Integer calories,
        @NotBlank
        String imageUrl
) {

}
