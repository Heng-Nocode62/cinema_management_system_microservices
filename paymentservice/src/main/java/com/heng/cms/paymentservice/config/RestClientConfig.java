package com.heng.cms.paymentservice.config;

import com.heng.cms.paymentservice.gateway.BakongGateway;
import com.heng.cms.paymentservice.gateway.KhqrPayGateway;
import com.heng.cms.paymentservice.gateway.KhqrPayGateway1;
import com.heng.cms.paymentservice.gateway.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Slf4j
@Configuration
public class RestClientConfig {
    @Value("${payment.gateway:khqrpay}")
    private String activeGateway;

    @Bean("bakongRestClient")
    public RestClient bakongRestClient(
            BakongProperties properties
    ) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + properties.token())
                .build();
    }

    @Bean("khqrPayRestClient")
    public RestClient khqrPayRestClient(KhqrPayProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.baseUrl())
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("ngrok-skip-browser-warning", "true")
                .build();
    }

    @Bean
    @Primary
    public PaymentGateway activePaymentGateway(
            @Qualifier("bakongGateway") BakongGateway bakongGateway,
            @Qualifier("khqrPayGateway") KhqrPayGateway khqrPayGateway
    ){
        PaymentGateway selectedGateway = switch (activeGateway){
            case "khqrpay" -> khqrPayGateway;
            case "bakong" -> bakongGateway;
            default -> throw new IllegalStateException("unknown gateway: " + activeGateway);
        };
        log.info("active gateway: {}", selectedGateway.gatewayName());
        return selectedGateway;
    }

}
