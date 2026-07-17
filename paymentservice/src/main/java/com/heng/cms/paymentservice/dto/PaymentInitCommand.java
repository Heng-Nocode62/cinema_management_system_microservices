package com.heng.cms.paymentservice.dto;

import java.math.BigDecimal;

public record PaymentInitCommand(
        String transactionId,
        BigDecimal amount,
        String currency,
        String description
) {
}
