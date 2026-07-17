package com.heng.cms.paymentservice.controller;

import com.heng.cms.paymentservice.gateway.KhqrPayGateway;
import com.heng.cms.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/payments/khqrpay")
@Slf4j
public class KhqrPayCallbackController {

    private final KhqrPayGateway khqrPayGateway;
    private final PaymentService paymentService;
    public KhqrPayCallbackController(KhqrPayGateway khqrPayGateway,PaymentService paymentService) {
        this.khqrPayGateway = khqrPayGateway;
        this.paymentService = paymentService;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> webhook(@RequestBody Map<String, String> body) {
        String transactionId = body.get("transaction_id");
        String amount        = body.get("amount");
        String status        = body.get("status");
        String signature     = body.get("signature");

        log.info("[KHQRPay] Webhook received: transactionId={} status={}", transactionId, status);

        // 1. Verify the signature first — reject if invalid
        if (!khqrPayGateway.verifyCallback(transactionId, amount, status, signature)) {
            log.warn("[KHQRPay] Invalid webhook signature for transactionId={}", transactionId);
            return ResponseEntity.ok("IGNORED");   // still 200 to prevent retry storm
        }

        // 2. Trigger a status poll — service handles DB update + Kafka event
        try {
            paymentService.checkPaymentStatus(
                    paymentService.getByBillNumber(transactionId).getPaymentId()
            );
        } catch (Exception e) {
            log.error("[KHQRPay] Webhook processing error for {}: {}", transactionId, e.getMessage());
        }

        return ResponseEntity.ok("OK");
    }

    @GetMapping("/cancel")
    public ResponseEntity<Map<String, String>> cancel(
            @RequestParam("transaction_id") String transactionId) {
        log.info("[KHQRPay] Payment cancelled: transactionId={}", transactionId);
        return ResponseEntity.ok(Map.of("transaction_id", transactionId, "result", "cancelled"));
    }
}