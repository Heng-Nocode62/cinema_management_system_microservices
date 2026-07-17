package com.heng.cms.paymentservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({BakongProperties.class,KhqrPayProperties.class})
public class AppConfig {

}
