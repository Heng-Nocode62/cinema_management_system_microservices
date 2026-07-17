package com.heng.cms.paymentservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InitiatePaymentRequest{
    @NotNull
    private UUID bookingId;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;

    @NotNull
    private String currency;

    private String description;
    private String promoCode;
}
