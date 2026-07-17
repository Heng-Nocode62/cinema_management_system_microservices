package com.heng.cms.concessionservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record RestClientProperties(
        String cinemaUrl
){
}
