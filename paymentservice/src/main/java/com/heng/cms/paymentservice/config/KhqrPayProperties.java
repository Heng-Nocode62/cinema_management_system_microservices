package com.heng.cms.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "khqrpay")
public record KhqrPayProperties(
        String baseUrl,
        String profileId,
        String apiKey,
        String successUrl,
        String cancelUrl,
        String webhookSecret,
        long qrExpiryMinutes
) {
}
