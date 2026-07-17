package com.heng.cms.paymentservice.dto.response;

import com.heng.cms.paymentservice.domain.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentStatusResponse {
    private UUID paymentId;
    private UUID bookingId;
    private PaymentStatus status;
    private BigDecimal amount;
    private BigDecimal totalRefunded;
    private String currency;
    private String gatewayName;
    private String billNumber;
    private String payerAccountId;
    private Instant createdAt;
    private Instant expiresAt;
}
