package com.heng.cms.bookingservice.config;

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
    @LoadBalanced
    public RestClient.Builder getRestClientBuilder() {

        return RestClient
                .builder()
                .requestInterceptor((request, body, execution) -> {
                    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                    if (authentication instanceof JwtAuthenticationToken jwt){
                        request.getHeaders().setBearerAuth(jwt.getToken().getTokenValue());

                    }
                    return execution.execute(request,body);
                });
    }
    @Bean(name = "schedulingRestClient")
    public RestClient schedulingRestClient(RestClientProperties properties,RestClient.Builder builder) {
        return builder.baseUrl(properties.getSchedulingUrl()).build();
    }

    @Bean(name = "paymentRestClient")
    public RestClient paymentRestClient(RestClientProperties properties,RestClient.Builder builder) {
        return builder.baseUrl(properties.getPaymentUrl()).build();
    }

    @Bean(name = "concessionRestClient")
    public RestClient concessionRestClient(RestClientProperties properties,RestClient.Builder builder) {
        return builder.baseUrl(properties.getConcessionUrl()).build();
    }
}
