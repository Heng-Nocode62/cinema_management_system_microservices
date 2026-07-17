package com.heng.cms.paymentservice.event;

import com.heng.cms.paymentservice.domain.Payment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.payment-confirmed}")
    private String confirmedTopic;
    @Value(("${kafka.topics.payment-failed}"))
    private String failedTopic;

    public void publishPaymentConfirmed(Payment payment) {
        kafkaTemplate.send(confirmedTopic, payment.getBookingId().toString(), buildEvent("PAYMENT_CONFIRMED",payment));
        log.info("Published PAYMENT_CONFIRMED for bookingId={}", payment.getBookingId());
    }

    public void publishPaymentFailed(Payment payment,String reason) {
        Map<String , Object> event = buildEvent("PAYMENT_FAILED",payment);
        event.put("reason",reason);
        kafkaTemplate.send(failedTopic, payment.getBookingId().toString(), event);
        log.info("Published PAYMENT_FAILED for bookingId={}", payment.getBookingId());
    }

    private Map<String,Object> buildEvent(String type, Payment payment) {
        Map<String,Object> event = new HashMap<>();
        event.put("eventId", UUID.randomUUID().toString());
        event.put("eventType", type);
        event.put("occurredAt", Instant.now().toString());
        event.put("paymentId", payment.getBookingId());
        event.put("bookingId", payment.getBookingId());
        event.put("userId", payment.getUserId());
        event.put("amount", payment.getAmount());
        event.put("currency", payment.getCurrency());
        event.put("status", payment.getStatus().toString());
        return event;
    }
}
