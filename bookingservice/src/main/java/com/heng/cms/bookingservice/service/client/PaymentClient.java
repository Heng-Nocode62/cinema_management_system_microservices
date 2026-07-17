package com.heng.cms.bookingservice.service.client;

import com.heng.cms.bookingservice.dto.client.InitiatePaymentCommand;
import com.heng.cms.bookingservice.dto.client.PaymentInitiateResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Map;

@Component
@Slf4j
public class PaymentClient {

    @Autowired
    @Qualifier("paymentRestClient")
    private RestClient paymentRestClient;

    public PaymentInitiateResponse initiatePayment(InitiatePaymentCommand command){
        return paymentRestClient.post()
                .uri("/api/v1/payments/initiate")
                .body(command)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .body(PaymentInitiateResponse.class);
    }

    public boolean validatePromoCode(String code, BigDecimal amount) {
        try {
            var result = paymentRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/promo-codes/validate")
                            .queryParam("code", code)
                            .queryParam("amount", amount)
                            .build())
                    .retrieve()
                    .body(Map.class);
            return result != null && Boolean.TRUE.equals(result.get("valid"));
        } catch (Exception e) {
            log.warn("Promo code validation failed: {}", e.getMessage());
            return false;
        }
    }

    public BigDecimal calculateDiscount(String promoCode, BigDecimal amount) {
        try {
            var result = paymentRestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/promo-codes/{code}/discount")
                            .queryParam("amount", amount)
                            .build(promoCode))
                    .retrieve()
                    .body(Map.class);
            if (result != null && result.get("discountAmount") != null) {
                return new BigDecimal(result.get("discountAmount").toString());
            }
        } catch (Exception e) {
            log.warn("Could not fetch discount for promo {}: {}", promoCode, e.getMessage());
        }
        return BigDecimal.ZERO;
    }

}
