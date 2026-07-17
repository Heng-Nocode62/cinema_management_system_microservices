package com.heng.cms.schedulingservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services")
public record RestClientProperties (
        String movieUrl,
        String cinemaUrl
){
}
