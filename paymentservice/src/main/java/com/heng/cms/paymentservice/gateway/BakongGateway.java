package com.heng.cms.paymentservice.gateway;

import com.heng.cms.paymentservice.config.BakongProperties;
import com.heng.cms.paymentservice.dto.*;
import com.heng.cms.paymentservice.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@Component("bakongGateway")
@RequiredArgsConstructor
@Slf4j
public class BakongGateway implements PaymentGateway {
    private final RestClient bakongRestClient;
    private final BakongProperties properties;
    @Override
    public GatewayInitResult initiatePayment(PaymentInitCommand command) {

        KhqrResult khqrResult = KhqrBuilder.buildKhqrResult(
                properties.merchantId(),
                properties.merchantName(),
                properties.merchantCity(),
                command.amount().doubleValue(),
                command.currency(),
                command.transactionId(),
                properties.storeLabel(),
                properties.terminalLabel()
        );

        Instant expiresAt = Instant.now().plus(properties.qrExpiryMinutes(),ChronoUnit.MINUTES);
        String deeplink = generateDeeplink(khqrResult.khqrString());

        log.info("[Bakong] khqr generated transactionId={} md5f={}",command.transactionId(),khqrResult.md5());
        return new GatewayInitResult(
                khqrResult.md5(),
                khqrResult.khqrString(),
                null,
                null,
                deeplink,
                expiresAt


        );
    }

    @Override
    public GatewayStatusResult checkStatus(String gatewayReference) {

        Map<String, Object> response = bakongRestClient.post()
                .uri("/v1/check_status_by_md5")
                .body(Map.of("md5", gatewayReference))
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {
                });

        return parseStatusResponse(response);
    }


    @Override
    public GatewayRefundResult refund(String gatewayRef, BigDecimal amount, String reason) {
        // Bakong has no refund API — manual bank transfer required
        log.warn("[Bakong] Refund requested for ref={} amount={} — must be processed manually via bank", gatewayRef, amount);
        return new GatewayRefundResult(
                true,  // optimistically mark as done — manual process outside system
                "Bakong does not support API refunds. Process manually via your bank portal.",
                amount
        );
    }

    @Override
    public String gatewayName() {
        return "Bakong NBC";
    }

    public String generateDeeplink(String khqrString) {
        try{
            Map response = bakongRestClient
                    .post()
                    .uri("/v1/generate_deeplink_by_qr")
                    .body(
                            Map.of(
                                    "qr",khqrString,
                                    "sourceInfo",Map.of(
                                            "appName", "CinemaHub",
                                            "appIconUrl", "https://cinemahub.com/icon.png",
                                            "appDeepLinkCallback", "https://cinemahub.com/payment/return"
                                    )

                            )
                    )
                    .retrieve()
                    .body(Map.class);
            if(response != null && Integer.valueOf(0).equals(response.get("responseCode"))){
                Map data = (Map) response.get("data");
                return data != null ? (String) data.get("shortLink"):null;
            }
        }catch(Exception e){
            log.warn("Deeplink generation failed (non-fetal): {}",e.getMessage());
        }

        return null;
    }


    private GatewayStatusResult parseStatusResponse(Map response) {
        if(response == null){ return pendingStatusResult("Empty Response");}
        int responseCode = toInt(response.get("responseCode"));
        int errorCode = toInt(response.getOrDefault("errorCode",-1));
        String message = (String) response.getOrDefault("responseMessage","");


        if (responseCode != 0) {
            GatewayPaymentStatus status = (errorCode==3)? GatewayPaymentStatus.FAILED : GatewayPaymentStatus.PENDING;
            return new GatewayStatusResult(status,null,null,null,null,message);
        }

        Map data =  (Map) response.get("data");
        if (data == null) {
            return pendingStatusResult("No data");
        }

        return  new GatewayStatusResult(
                GatewayPaymentStatus.SUCCESS,
                (String) data.get("hash"),
                (String) data.get("fromAccountId"),
                data.get("amount") != null? new BigDecimal(data.get("amount").toString()):null,
                (String) data.get("currency"),
                message

        );


    }
    private GatewayStatusResult pendingStatusResult(String message){
        return new GatewayStatusResult(GatewayPaymentStatus.PENDING,null,null,null,null, message);
    }

    private int toInt(Object value){
        if (value==null){
            return -1;
        }
        return ((Number)value).intValue();
    }


}
