package com.heng.cms.concessionservice.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
    @Bean
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    @LoadBalanced
    public RestClient.Builder getRestClientBuilder(){
        return RestClient.builder();
    }

    @Bean
    public RestClient cinemaRestClient(RestClientProperties properties) {
        return getRestClientBuilder()
                .baseUrl(properties.cinemaUrl())
                .requestInterceptor((request, body, execution) -> {
                    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                    if (auth instanceof JwtAuthenticationToken jwt){
                        request.getHeaders().setBearerAuth(jwt.getToken().getTokenValue());
                    }
                    return execution.execute(request,body);
                })
                .build();

    }
}
