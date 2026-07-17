package com.heng.cms.paymentservice.controller;

import com.heng.cms.paymentservice.domain.PromoCode;
import com.heng.cms.paymentservice.dto.request.InitiatePaymentRequest;
import com.heng.cms.paymentservice.dto.request.RefundRequest;
import com.heng.cms.paymentservice.dto.response.PaymentInitiateResponse;
import com.heng.cms.paymentservice.dto.response.PaymentStatusResponse;
import com.heng.cms.paymentservice.service.PaymentService;
import com.heng.cms.paymentservice.service.PromoCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;
    private final PromoCodeService promoCodeService;

    @PostMapping("/initiate")
    public ResponseEntity<PaymentInitiateResponse> initiatePayment(
            @Valid
            @RequestBody
            InitiatePaymentRequest request
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiatePayment(request));
    }

    @GetMapping("/{paymentId}/status")
    public ResponseEntity<PaymentStatusResponse> checkPaymentStatus(
            @PathVariable("paymentId") UUID paymentId
    ){
        return ResponseEntity.ok(paymentService.checkPaymentStatus(paymentId));
    }

    @GetMapping("{paymentId}")
    public ResponseEntity<PaymentStatusResponse> getByPaymentId(
            @RequestParam("paymentId") UUID paymentId
    ){
        return ResponseEntity.ok(paymentService.getById(paymentId));
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<PaymentStatusResponse> getByBookingId(
            @PathVariable("bookingId")  UUID bookingId
    ){
        return ResponseEntity.ok(paymentService.getByBookingId(bookingId));
    }


//    @PostMapping("/refund")
//    public ResponseEntity<PaymentStatusResponse> refund(
//            @Valid
//            @RequestBody
//            RefundRequest request
//    ){
//        paymentService.refund(request);
//        return ResponseEntity.notFound().build();
//    }

    @GetMapping("/promo-codes/validate")
    public ResponseEntity<Map<String, Object>> validatePromoCode(
            @RequestParam String code,
            @RequestParam BigDecimal amount
    ){
        return ResponseEntity.ok(Map.of("valid",promoCodeService.validate(code,amount)));
    }

    @GetMapping("/promo-codes/{code}/discount")
    public ResponseEntity<Map<String, Object>> getDiscount(
            @PathVariable("code") String code,
            @RequestParam BigDecimal amount
    ){
        return ResponseEntity.ok(promoCodeService.getDiscount(code,amount));
    }
}
