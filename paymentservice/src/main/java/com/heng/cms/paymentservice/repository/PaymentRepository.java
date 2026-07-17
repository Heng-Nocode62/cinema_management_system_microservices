package com.heng.cms.paymentservice.repository;

import com.heng.cms.paymentservice.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByBookingId(UUID bookingId);
    Optional<Payment> findByBillNumber(String billNumber);

    boolean existsByBookingIdAndStatus(UUID bookingId, String status);


    @Query("""
        SELECT p
        FROM Payment p
                WHERE p.status = 'PENDING'
                AND p.qrExpiresAt > :now
        """)
    List<Payment> findActivePendingPayment(@Param("now") Instant now);

    @Modifying
    @Query("""
        UPDATE Payment p
        SET p.status = 'EXPIRED'
                WHERE p.status = 'PENDING'
                AND p.qrExpiresAt < :now
        """)
    int expireStalePayments(@Param("now") Instant now);

}
