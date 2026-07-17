package com.heng.cms.paymentservice.dto;

import java.math.BigDecimal;

public record GatewayRefundResult(
        boolean success,
        String message,
        BigDecimal refundedAmount
) {
}
