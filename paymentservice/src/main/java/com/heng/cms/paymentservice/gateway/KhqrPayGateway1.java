package com.heng.cms.paymentservice.gateway;

import com.heng.cms.paymentservice.config.KhqrPayProperties;
import com.heng.cms.paymentservice.dto.*;
import com.heng.cms.paymentservice.exception.PaymentException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class KhqrPayGateway1{
    private final KhqrPayProperties properties;

    private final RestClient khqrPayRestClient;


    public GatewayInitResult initiatePayment(PaymentInitCommand command) {
        String amountString = command.amount().toPlainString();
//        String hash = signPurchase(command.transactionId(), amountString, command.currency());

        String u = properties.apiKey()+command.transactionId()+amountString+properties.successUrl()+"Jonh";
        String hash = sha1(u);
        log.info("KhqrPayGateway initiatePayment, hash: {}", u);
        log.info("KhqrPayGateway initiatePayment, hash: {}", hash);
//        String hash = hmacSha1(properties.apiKey(), command.transactionId()+amountString+properties.successUrl()+"Jonh");
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("transaction_id", command.transactionId());
        form.add("amount", amountString);
//        form.add("currency", command.currency()!= null?command.currency():"USD");
        form.add("success_url",properties.successUrl());
        form.add("remark","Jonh");
        form.add("hash",hash);

//        form.add("cancel_url",properties.cancelUrl());
//        form.add("hash", hash);
//
//        if (command.customerEmail() != null) form.add("email", command.customerEmail());
//        if (command.customerFirstName() != null) form.add("first_name", command.customerFirstName());
//        if (command.customerLastName() != null) form.add("last_name", command.customerLastName());
//        if (command.customerPhone()!= null) form.add("phone", command.customerPhone());
//        if (command.description() != null) form.add("note", command.description());

        Map response =  khqrPayRestClient.post()
                .uri("/api/{profileId}/payment-gateway/v1/payments/qr-api",properties.profileId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(s -> s.value() == 401, (req, resp) -> {

                    throw new PaymentException("[KHQRPay] Invalid API key or profile ID");
                })
                .onStatus(s -> !s.is2xxSuccessful(), (req, resp) -> {
                    log.info("uri={}", req.getURI());
                    log.info("message = {}", new String(resp.getBody().readAllBytes(), StandardCharsets.UTF_8));
                    throw new PaymentException("[KHQRPay] Purchase request failed: HTTP " + resp.getStatusCode());
                })
                .body(Map.class);

        if (response == null || !isSuccess(response)){
            throw new PaymentException("[KHQRPay] Purchase failed: " + errorMessage(response));
        }
        Map data = (Map) response.get("data");
        String qrString = data != null?(String) data.get("qr_string"):null;
        String qrImageBase64 = data != null?(String) data.get("qr_image"):null;
        String deeplink =  data != null?(String) data.get("deeplink"):null;
        String checkoutUrl =  data != null?(String) data.get("checkout_url"):null;
        log.info("[KHQRPay] Purchase initiated: transactionId={}", command.transactionId());

        return new GatewayInitResult(
                command.transactionId(),
                qrString,qrImageBase64,checkoutUrl,deeplink, Instant.now().plus(properties.qrExpiryMinutes(), ChronoUnit.MINUTES)
        );

    }

    private boolean isSuccess(Map response) {
        if (response == null) return false;
        Object status = response.get("status");
        if (status instanceof Number n) return n.intValue() == 0 || n.intValue() == 200;
        if (status instanceof String s) return "0".equals(s) || "success".equalsIgnoreCase(s);
        return false;
    }


    public GatewayStatusResult checkStatus(String gatewayReference) {
        String hash = signCheck(gatewayReference);

       Map response = khqrPayRestClient.post()
                .uri("{profileId}/payment-gateway/v1/payments/qr-api",properties.profileId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(buildCheckForm(gatewayReference,hash))
                .retrieve()
                .body(Map.class);

       if (response == null){
           return new GatewayStatusResult(GatewayPaymentStatus.PENDING,gatewayReference,null,null,null,"Empty Response");
       }

        Map data = (Map) response.get("data");
        String status = data!= null ? (String) data.get("status") : null;
        String payerId= data!= null ? (String) data.get("payer_account") : null;
        String amountString = data!= null ? (String) data.get("amountString") : null;
        String currency = data!= null ? (String) data.get("currency") : null;


        GatewayPaymentStatus gStatus = switch (status!=null? status.toUpperCase():""){
            case "COMPLETED","SUCCESS","PAID" -> GatewayPaymentStatus.SUCCESS;
            case "FAILED","DECLINED" -> GatewayPaymentStatus.FAILED;
            case "EXPIRED" -> GatewayPaymentStatus.EXPIRED;
            default -> GatewayPaymentStatus.PENDING;

        };

        return new GatewayStatusResult(
                gStatus,
                gatewayReference,
                payerId,amountString != null ? new BigDecimal(amountString):null,
                currency,
                status
        );
    }



    public GatewayRefundResult refund(String gatewayReference, BigDecimal amount, String reason) {
        String amountStr = amount.toPlainString();
        String hash = signRefund(gatewayReference, amountStr);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("transaction_id", gatewayReference);
        form.add("amount",         amountStr);
        form.add("reason",         reason);
        form.add("hash",           hash);

        Map<?, ?> response = khqrPayRestClient.post()
                .uri("/{profileId}/payment-gateway/v1/payments/refund", properties.profileId())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(Map.class);

        boolean success = isSuccess(response);
        String message  = errorMessage(response);

        log.info("[KHQRPay] Refund {}: transactionId={} amount={}", success ? "SUCCESS" : "FAILED", gatewayReference, amount);

        return new GatewayRefundResult(success, message, success ? amount : BigDecimal.ZERO);
    }

    private String signRefund(String gatewayReference, String amountStr) {
        return hmacSha1(properties.apiKey(),gatewayReference+amountStr);
    }


    private MultiValueMap<String,String> buildCheckForm(String gatewayReference, String hash) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("transaction_id", gatewayReference);
        form.add("hash", hash);
        return form;
    }

    private String signCheck(String gatewayReference) {
        return hmacSha1(properties.apiKey(), gatewayReference);
    }



    public String gatewayName() {
        return "KHQRPay.cc";
    }

    public boolean verifyWebhookSignature(String transactionId, String amount,
                                          String status, String receivedSig) {
        String payload = transactionId + amount + status;
        String expected = hmacSha1(properties.webhookSecret(), payload);
        return expected.equalsIgnoreCase(receivedSig);
    }


    private String signPurchase(String transactionId,String amount,String currency){
        String data = transactionId+ amount + (currency!=null ? currency:"USD");
        return hmacSha1(properties.apiKey(),data);
    }


    private String hmacSha1(String key, String data){
        try{
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8),"HmacSHA1"));
            byte[] raw = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(raw);

        } catch (Exception e) {
            throw new RuntimeException("Hmac SHA1 sign failed",e);
        }
    }
    private String errorMessage(Map response){
        if (response == null) return "Null response";
        Object msg = response.get("message");
        return msg !=null ? msg.toString():"Unknown error";
    }
    public static String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

    }
}
