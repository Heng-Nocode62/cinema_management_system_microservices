package com.heng.cms.bookingservice.event;


import com.heng.cms.bookingservice.service.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class PaymentEventListener {
    private final BookingService bookingService;

    @Transactional
    @KafkaListener(topics = "${kafka.topics.payment-confirmed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentConfirmed(Map<String, Object> event) {
        String bookingId = (String) event.get("bookingId");
        String paymentStatus = (String) event.get("status");
        log.info("Received payment event for bookingId={} status={}", bookingId, paymentStatus);

        if("SUCCESS".equals(paymentStatus)){
            bookingService.confirmBooking(UUID.fromString(bookingId));
        }else if("FAILED".equals(paymentStatus)){
            bookingService.cancelBookingById(UUID.fromString(bookingId));
        }


    }


}
