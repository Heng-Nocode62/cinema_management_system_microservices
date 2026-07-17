package com.heng.cms.cinemaservice.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->auth
                        .requestMatchers(HttpMethod.GET,"/**").permitAll()
                        .requestMatchers("/api/v1/cinemas/batch").permitAll()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oath2->oath2.jwt(
                        Customizer.withDefaults()
                ))
                .build();


    }
}
