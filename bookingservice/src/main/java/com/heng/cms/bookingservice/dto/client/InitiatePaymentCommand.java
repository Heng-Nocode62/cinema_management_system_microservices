package com.heng.cms.bookingservice.dto.client;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record InitiatePaymentCommand(
        @NotNull
         UUID bookingId,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal amount,

        @NotNull
        String currency,

        String description,
        String promoCode
){

}