package com.heng.cms.paymentservice.service;

import com.heng.cms.paymentservice.config.BakongProperties;
import com.heng.cms.paymentservice.domain.Payment;
import com.heng.cms.paymentservice.domain.Refund;
import com.heng.cms.paymentservice.domain.enums.PaymentStatus;
import com.heng.cms.paymentservice.domain.enums.RefundStatus;
import com.heng.cms.paymentservice.dto.BakongTransactionResult;
import com.heng.cms.paymentservice.dto.GatewayInitResult;
import com.heng.cms.paymentservice.dto.GatewayStatusResult;
import com.heng.cms.paymentservice.dto.PaymentInitCommand;
import com.heng.cms.paymentservice.dto.request.InitiatePaymentRequest;
import com.heng.cms.paymentservice.dto.request.RefundRequest;
import com.heng.cms.paymentservice.dto.response.PaymentInitiateResponse;
import com.heng.cms.paymentservice.dto.response.PaymentStatusResponse;
import com.heng.cms.paymentservice.event.PaymentEventPublisher;
import com.heng.cms.paymentservice.exception.PaymentException;
import com.heng.cms.paymentservice.gateway.PaymentGateway;
import com.heng.cms.paymentservice.repository.PaymentRepository;
import com.heng.cms.paymentservice.repository.RefundRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final PaymentGateway paymentGateway;
    private final PaymentEventPublisher paymentEventPublisher;
    private final BakongProperties bakongProperties;

    @Transactional
    public PaymentInitiateResponse initiatePayment(InitiatePaymentRequest request) {
        // Idempotency guard
        paymentRepository.findByBookingId(request.getBookingId())
                .ifPresent(existingPayment -> {
                    if (existingPayment.getStatus() == PaymentStatus.PENDING) {
                        throw new PaymentException("A pending KHQR payment already exists for booking" + request.getBookingId());
                    }
                    if (existingPayment.getStatus() == PaymentStatus.SUCCESS) {
                        throw new PaymentException("Booking " + request.getBookingId() + " is already paid");
                    }
                });

        // unique bill number : first 20 chars of bookingId (no dashes)
        // improve later TODO if the first 20 unit of booking id are the same, that would violate uniqueness
        String billNumber = request.getBookingId().toString().replace("-", "").substring(0, 20);

        //build khqr string
        String currency = request.getCurrency() != null ? request.getCurrency() : "USD";


        PaymentInitCommand command = new PaymentInitCommand(
                billNumber,
                request.getAmount(),
                currency,
                request.getDescription()
        );

        UUID userAccountId = extractUserIdFromToken();

        GatewayInitResult gatewayResult = paymentGateway.initiatePayment(command);
        // persist the payment record
        Payment payment = Payment.builder()
                .bookingId(request.getBookingId())
                .userId(userAccountId)
                .amount(request.getAmount())
                .currency(currency)
                .status(PaymentStatus.PENDING)
                .gatewayName(paymentGateway.gatewayName())
                .gatewayReference(gatewayResult.gatewayReference())
                .billNumber(billNumber)
                .checkoutUrl(gatewayResult.checkoutUrl())
                .qrString(gatewayResult.qrString())
                .qrImageBase64(gatewayResult.qrImageB64())
                .deeplink(gatewayResult.deeplink())
                .expiresAt(gatewayResult.expiresAt())
                .qrExpiresAt(gatewayResult.expiresAt())
                .build();
        paymentRepository.save(payment);
        log.info("KHQR payment initiated: paymentId={} bookingId={} amount={} {}", payment.getId(), payment.getBookingId(), payment.getAmount(), payment.getCurrency());

        return PaymentInitiateResponse.builder()
                .paymentId(payment.getId())
                .billNumber(billNumber)
                .gatewayName(payment.getGatewayName())
                .khqrString(payment.getQrString())
                .khqrDeeplink(payment.getDeeplink())
                .checkoutUrl(payment.getCheckoutUrl())
                .amount(request.getAmount())
                .currency(currency)
                .expiresAt(payment.getExpiresAt())
                .status(PaymentStatus.PENDING.name())
                .build();

    }

    private UUID extractUserIdFromToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("userAccountId");
        return UUID.fromString(userId);
    }

    public PaymentStatusResponse checkPaymentStatus(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(()-> new PaymentException("Payment with id: " + paymentId + " not found"));

        if (payment.getStatus() == PaymentStatus.PENDING && payment.getGatewayReference() != null) {
            pollBakongAndUpdate(payment);

        }
        return toPaymentStatusResponse(payment);
    }


    // background poller - run every 3s, polls all active pending qrs

    @Scheduled(fixedDelayString = "${bakong.poll-interval-seconds:3}000")
    @Transactional
    public void pollPendingPayments() {
        List<Payment> pendingPayments = paymentRepository.findActivePendingPayment(Instant.now());
        if (!pendingPayments.isEmpty()) {
            log.debug("Polling {} pending payments", pendingPayments.size());
            pendingPayments.forEach(this::pollBakongAndUpdate);
        }
    }

    // run every minute to make time-out qr payments as EXPIRED
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void expiredStaleQrPayments() {
        int count = paymentRepository.expireStalePayments(Instant.now());
        if(count > 0) {
            log.info("Marked {} QR payments as EXPIRED", count);
        }
    }

//    @Transactional
//    public void refund(RefundRequest request) {
//        Payment payment = paymentRepository.findById(request.getPaymentId())
//                .orElseThrow(() -> new PaymentException("Payment not found id: " + request.getPaymentId()));
//        if (payment.getStatus() != PaymentStatus.SUCCESS && payment.getStatus() != PaymentStatus.PARTIALLY_REFUNDED){
//            throw new PaymentException("Only successful refunds can be refunded!");
//        }
//
//
//        BigDecimal remaining = payment.getAmount().subtract(payment.totalRefunded());
//        if (request.getAmount().compareTo(remaining) > 0) {
//            throw new PaymentException("Refund amount "+ request.getAmount()+" exceeds payment amount "+ remaining);
//        }
//
//        // this is dangerous
//        Refund refund = Refund.builder()
//                .payment(payment)
//                .amount(request.getAmount())
//                .reason(request.getReason())
//                .status(RefundStatus.SUCCESS)
//                .completedAt(Instant.now())
//                .build();
//        refundRepository.save(refund);
//
//        BigDecimal newTotal = payment.totalRefunded().add(request.getAmount());
//        payment.setStatus(newTotal.compareTo(payment.getAmount())>= 0 ? PaymentStatus.REFUNDED:PaymentStatus.PARTIALLY_REFUNDED);
//        paymentRepository.save(payment);
//        log.info("Refund recorded: paymentId={} amount={}", payment.getId(), payment.getAmount());
//    }

    public PaymentStatusResponse getById(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentException("Payment not found id: " + paymentId));

        return toPaymentStatusResponse(payment);
    }

    public PaymentStatusResponse getByBookingId(UUID bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new PaymentException("Payment not found id: " + bookingId));
        return toPaymentStatusResponse(payment);
    }
    public PaymentStatusResponse getByBillNumber(String billNumber) {
        Payment payment = paymentRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new PaymentException("Payment not found id: " + billNumber));
        return toPaymentStatusResponse(payment);
    }

    @Transactional
    private void pollBakongAndUpdate(Payment payment) {
        // guard: skip if already  in a terminal state
        if (payment.getStatus() != PaymentStatus.PENDING) return;
        // guard: skip if qr has expired
        if (payment.getQrExpiresAt() != null && Instant.now().isAfter(payment.getQrExpiresAt())) {
            payment.setStatus(PaymentStatus.EXPIRED);
            paymentRepository.save(payment);
            return;
        }

        try {
            GatewayStatusResult result = paymentGateway.checkStatus(payment.getGatewayReference());
            switch (result.status()){
                case SUCCESS->{
                    payment.setStatus(PaymentStatus.SUCCESS);
                    payment.setPayerAccountId(result.payerAccountId());
                    payment.setConfirmedAmount(result.paidAmount());
                    payment.setConfirmedCurrency(result.paidCurrency());
                    payment.setPaidAt(Instant.now());
                    paymentRepository.save(payment);
                    paymentEventPublisher.publishPaymentConfirmed(payment);
                    log.info("[{}] Payment confirmed: paymentId={} payer={}",
                            paymentGateway.gatewayName(), payment.getId(), result.payerAccountId());
                }
                case FAILED->{
                    payment.setStatus(PaymentStatus.FAILED);
                    payment.setErrorMessage(result.rawMessage());
                    paymentRepository.save(payment);
                    paymentEventPublisher.publishPaymentFailed(payment, result.rawMessage());
                    log.warn("[{}] Payment failed: paymentId={}", paymentGateway.gatewayName(), payment.getId());
                }
                case EXPIRED -> {
                    payment.setStatus(PaymentStatus.EXPIRED);
                    paymentRepository.save(payment);
                }
                case PENDING -> {
                    log.debug("[{}] Still pending: paymentId={}",
                            paymentGateway.gatewayName(), payment.getId());
                }
            }
        }catch (Exception e){
            log.error("[{}] Error polling payment {}: {}", paymentGateway.gatewayName(), payment.getId(), e.getMessage());
        }
    }

    private PaymentStatusResponse toPaymentStatusResponse(Payment payment) {
        return PaymentStatusResponse.builder()
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .status(payment.getStatus())
                .amount(payment.getAmount())
                .totalRefunded(payment.totalRefunded())
                .currency(payment.getCurrency())
                .gatewayName(payment.getGatewayName())
                .billNumber(payment.getBillNumber())
                .payerAccountId(payment.getPayerAccountId())
                .createdAt(payment.getCreatedAt())
                .expiresAt(payment.getExpiresAt())
                .build();
    }


}
