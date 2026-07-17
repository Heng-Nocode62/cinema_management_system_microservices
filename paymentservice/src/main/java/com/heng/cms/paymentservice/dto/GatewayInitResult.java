package com.heng.cms.paymentservice.dto;

import java.time.Instant;

public record GatewayInitResult(
        String gatewayReference,
        String qrString,
        String qrImageB64,
        String checkoutUrl,
        String deeplink,
        Instant expiresAt
) {
}
