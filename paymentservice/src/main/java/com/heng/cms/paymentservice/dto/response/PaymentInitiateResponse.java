package com.heng.cms.paymentservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class PaymentInitiateResponse {
    private UUID paymentId;
    private String billNumber;
    private String gatewayName;
    private String khqrString;
    private String qrImageBase64;
    private String khqrDeeplink;
    private String checkoutUrl;
    private BigDecimal amount;
    private String currency;
    private Instant expiresAt;
    private String status;
}
