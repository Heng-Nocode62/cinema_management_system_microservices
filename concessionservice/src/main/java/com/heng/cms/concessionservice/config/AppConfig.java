package com.heng.cms.concessionservice.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({RestClientProperties.class})
public class AppConfig {
}
