package com.heng.cms.paymentservice.gateway;
import com.heng.cms.paymentservice.dto.*;
import com.heng.cms.paymentservice.exception.PaymentException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;

@Component("khqrPayGateway")
@Slf4j
public class KhqrPayGateway implements PaymentGateway {

    private final RestClient khqrPayRestClient;

    @Value("${khqrpay.profile-id}")  private String profileId;
    @Value("${khqrpay.api-key}")  private String secretKey;
    @Value("${khqrpay.success-url}") private String successUrl;

    private static final int QR_EXPIRY_MINUTES = 10;

    public KhqrPayGateway(
            @Qualifier("khqrPayRestClient") RestClient khqrPayRestClient) {
        this.khqrPayRestClient = khqrPayRestClient;
    }

    @Override
    public String gatewayName() { return "KHQRPay.cc"; }


    @Override
    public GatewayInitResult initiatePayment(PaymentInitCommand cmd) {
        String amountStr = cmd.amount().toPlainString();
        String remark    = cmd.description() != null ? cmd.description() : "Cinema Ticket";

        // SHA1(secretKey + transactionId + amount + successUrl + remark)
        String hash = sha1(secretKey + cmd.transactionId() + amountStr + successUrl + remark);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("transaction_id", cmd.transactionId());
        form.add("amount",         amountStr);
        form.add("success_url",    successUrl);
        form.add("remark",         remark);
        form.add("hash",           hash);

        log.info("[KHQRPay] initiating: transactionId={} amount={}", cmd.transactionId(), amountStr);

        Map<?, ?> response = khqrPayRestClient.post()
                .uri("/api/{profileId}/payment-gateway/v1/payments/qr-api", profileId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(s -> s.value() == 401, (req, resp) -> {
                    throw new PaymentException("[KHQRPay] 401 — check secret-key and profile-id");
                })
                .onStatus(s -> s.value() == 404, (req, resp) -> {
                    throw new PaymentException("[KHQRPay] 404 — wrong profile-id or endpoint");
                })
                .onStatus(s -> !s.is2xxSuccessful(), (req, resp) -> {
                    log.info("ERror :{}",resp.getStatusCode().toString(),new String(resp.getBody().readAllBytes(),StandardCharsets.UTF_8));
                    throw new PaymentException("[KHQRPay] HTTP " + resp.getStatusCode());
                })
                .body(Map.class);

        log.info("[KHQRPay] qr-api response: {}", response);

        if (response == null || !isSuccess(response)) {
            throw new PaymentException("[KHQRPay] Purchase failed: " +
                    (response != null ? response.get("responseMessage") : "null response"));
        }

        Map<?, ?> data = (Map<?, ?>) response.get("data");
        if (data == null) throw new PaymentException("[KHQRPay] No data in response");

        String qrString = str(data, "qr");       // raw KHQR EMVCo string → render as QR image
        String qrUrl    = str(data, "qr_url");   // hosted QR page URL

        log.info("[KHQRPay] QR ready: transactionId={}", cmd.transactionId());


        return new GatewayInitResult(
                cmd.transactionId(), // gatewayRef = transactionId (used in check-trans)
                qrString,
                null,
                qrUrl,
                null,
                Instant.now().plus(QR_EXPIRY_MINUTES, ChronoUnit.MINUTES)
        );
    }


    @Override
    public GatewayStatusResult checkStatus(String gatewayRef) {
        // gatewayRef = transactionId (our billNumber)
        // Hash: SHA1(secretKey + transactionId)
        String hash = sha1(secretKey + gatewayRef);

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("transaction_id", gatewayRef);
        form.add("hash",           hash);

        log.debug("[KHQRPay] check-trans: transactionId={}", gatewayRef);

        Map<?, ?> response = khqrPayRestClient.post()
                .uri("/api/{profileId}/payment-gateway/v1/payments/check-trans", profileId)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .onStatus(s -> !s.is2xxSuccessful(), (req, resp) -> {
                    throw new PaymentException("[KHQRPay] check-trans HTTP " + resp.getStatusCode());
                })
                .body(Map.class);

        log.info("[KHQRPay] check-trans response: {}", response);

        if (response == null) return pending(gatewayRef, "empty response");

        int responseCode = toInt(response.get("responseCode"));

        // responseCode 1 = "Transaction Not Found" — still pending, not an error
        if (responseCode != 0) {
            return pending(gatewayRef, str(response, "responseMessage"));
        }

        Map<?, ?> data = (Map<?, ?>) response.get("data");
        if (data == null) return pending(gatewayRef, "no data");

        //   Confirmed success condition from KHQRPay docs:
        //    responseCode == 0  AND  data.status == "success"
        String status = str(data, "status");
        if (!"success".equalsIgnoreCase(status)) {
            return pending(gatewayRef, "data.status=" + status);
        }

        // Extract payment details
        Map<?, ?> details   = data.get("payment_details") instanceof Map<?,?> m ? m : null;
        String sender       = str(details, "sender");       // payer Bakong ID / ABA ID
        String bakongHash   = str(details, "bakonghash");  // "N/A" for ABA Pay
        String paidAmount   = str(data,    "amount");
        String currency     = str(data,    "currency");

        log.info("[KHQRPay] payment confirmed: transactionId={} sender={} amount={} {}",
                gatewayRef, sender, paidAmount, currency);

        return new GatewayStatusResult(
                GatewayPaymentStatus.SUCCESS,
                // use bakongHash as the final ref if available, otherwise our transactionId
                (bakongHash != null && !"N/A".equalsIgnoreCase(bakongHash))
                        ? bakongHash : gatewayRef,
                sender,
                paidAmount != null ? new BigDecimal(paidAmount) : null,
                currency,
                str(response, "responseMessage")
        );
    }

    // ── Refund ────────────────────────────────────────────────────────────

    @Override
    public GatewayRefundResult refund(String gatewayRef, BigDecimal amount, String reason) {
        log.warn("[KHQRPay] No refund API — process manually via dashboard. ref={}", gatewayRef);
        return new GatewayRefundResult(
                true,
                "No refund API — process manually via KHQRPay dashboard",
                amount
        );
    }

    // ── Callback verification ─────────────────────────────────────────────

    /**
     * Verify the success_url redirect came from KHQRPay.
     * Always call check-trans API after this — never trust the callback alone.
     *
     * Hash formula: SHA1(secretKey + transactionId + amount + status)
     */
    public boolean verifyCallback(String transactionId, String amount,
                                  String status, String receivedHash) {
        if (receivedHash == null) return true; // KHQRPay may not always send hash
        String expected = sha1(secretKey + transactionId + amount + status);
        boolean valid   = expected.equalsIgnoreCase(receivedHash);
        if (!valid) log.warn("[KHQRPay] callback hash mismatch for transactionId={}", transactionId);
        return valid;
    }


    private GatewayStatusResult pending(String ref, String msg) {
        return new GatewayStatusResult(
                GatewayPaymentStatus.PENDING, ref, null, null, null, msg);
    }

    private String sha1(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return HexFormat.of().formatHex(
                    md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("SHA-1 failed", e);
        }
    }

    private String str(Map<?, ?> map, String... keys) {
        if (map == null) return null;
        for (String key : keys) {
            Object val = map.get(key);
            if (val != null && !val.toString().isBlank()) return val.toString();
        }
        return null;
    }

    private boolean isSuccess(Map<?, ?> response) {
        Object code = response.get("responseCode");
        if (code instanceof Number n) return n.intValue() == 0;
        if (code instanceof String s) return "0".equals(s);
        return false;
    }

    private int toInt(Object val) {
        if (val == null) return -1;
        if (val instanceof Number n) return n.intValue();
        try { return Integer.parseInt(val.toString()); } catch (Exception e) { return -1; }
    }
}