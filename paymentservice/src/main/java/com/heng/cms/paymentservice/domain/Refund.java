package com.heng.cms.paymentservice.domain;

import com.heng.cms.paymentservice.domain.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name="refunds")
public class Refund {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false, precision = 10,scale = 2)
    private BigDecimal amount;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RefundStatus status = RefundStatus.PENDING;

    @Column(name = "error_message")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "initiated_at",updatable = false)
    private Instant initiatedAt;

    @Column(name = "completed_at")
    private Instant completedAt;





}
