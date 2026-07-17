package com.heng.cms.schedulingservice.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties({RestClientProperties.class})
public class AppConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {

        return RestClient.builder();
    }

    @Bean
    public RestClient movieRestClient(
            RestClient.Builder builder,
            RestClientProperties properties) {
        return builder
                .requestInterceptor((request, body, execution) -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if(authentication instanceof JwtAuthenticationToken jwtAuth) {
                        request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
                    }
                    return execution.execute(request,body);
                })
                .baseUrl(properties.movieUrl())
                .build();
    }

    @Bean
    public RestClient cinemaRestClient(
            RestClient.Builder builder,
            RestClientProperties properties) {

        return builder
                .requestInterceptor((request, body, execution) -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if(authentication instanceof JwtAuthenticationToken jwtAuth) {
                        request.getHeaders().setBearerAuth(jwtAuth.getToken().getTokenValue());
                    }
                    return execution.execute(request,body);
                })
                .baseUrl(properties.cinemaUrl())
                .build();
    }
}
