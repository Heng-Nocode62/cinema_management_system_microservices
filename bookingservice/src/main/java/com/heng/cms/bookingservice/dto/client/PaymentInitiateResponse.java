package com.heng.cms.bookingservice.dto.client;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentInitiateResponse(
         UUID paymentId,
         String billNumber,
         String gatewayName,
         String khqrString,
         String qrImageBase64,
         String khqrDeeplink,
         String checkoutUrl,
         BigDecimal amount,
         String currency,
         Instant expiresAt,
         String status
) {

}