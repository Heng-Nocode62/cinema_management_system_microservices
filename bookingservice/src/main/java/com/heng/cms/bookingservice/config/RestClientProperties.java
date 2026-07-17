package com.heng.cms.bookingservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;

@ConfigurationProperties(prefix = "services")
@Getter
@NoArgsConstructor
@Setter
public class RestClientProperties {
    private String schedulingUrl;
    private String paymentUrl;
    private String concessionUrl;
}
