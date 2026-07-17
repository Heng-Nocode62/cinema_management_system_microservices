package com.heng.cms.movieservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }
}
