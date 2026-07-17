package com.heng.cms.paymentservice.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RefundRequest {

    @NotNull
    private UUID paymentId;
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    @NotBlank
    private String reason;

}
