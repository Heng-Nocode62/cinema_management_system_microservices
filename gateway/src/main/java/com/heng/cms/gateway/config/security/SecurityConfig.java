package com.heng.cms.gateway.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
                .authorizeExchange(auth->auth
                        .anyExchange().permitAll()
                )
                .oauth2ResourceServer(auth->auth.jwt(Customizer.withDefaults()))
                .csrf(csrf->csrf.disable())
                .build();
    }
}
