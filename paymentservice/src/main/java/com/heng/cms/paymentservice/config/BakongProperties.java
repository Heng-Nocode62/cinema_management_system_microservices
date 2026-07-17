package com.heng.cms.paymentservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bakong")
public record BakongProperties(

    String baseUrl,
    String token,
    String merchantId,
    String merchantName,
    String merchantCity,
    String storeLabel,
    String terminalLabel,
    int qrExpiryMinutes,
    int pollIntervalSecond
) {
}
