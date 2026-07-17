package com.heng.cms.paymentservice.domain;

import com.heng.cms.paymentservice.domain.enums.PaymentStatus;
import com.heng.cms.paymentservice.domain.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="payments")
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "booking_id",nullable = false)
    private UUID bookingId;

    @Column(name = "user_id",nullable = false)
    private UUID userId;

    @Column(nullable = false, precision = 10,scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name = "gateway_name",length = 30)
    private String gatewayName;

    @Column(name = "gateway_reference", length = 100,unique = true)
    private String gatewayReference;

    @Column(name = "bill_number", unique = true,length = 50)
    private String billNumber;


    @Column(name = "qr_string",columnDefinition = "TEXT")
    private String qrString;
    @Column(name = "qr_image_b64", columnDefinition = "TEXT")
    private String qrImageBase64;

    @Column(name = "deeplink", columnDefinition = "TEXT")
    private String deeplink;

    @Column(name = "checkout_url", columnDefinition = "TEXT")
    private String checkoutUrl;

    @Column(name = "qr_expires_at")
    private Instant qrExpiresAt;

    @Column(name = "payer_acount_id",length = 100)
    private String payerAccountId;

    @Column(name = "confirmed_amount",precision = 10,scale = 2)
    private BigDecimal confirmedAmount;

    @Column(name = "confirmed_currency",length = 3)
    private String confirmedCurrency;

    @Column(name = "error_message",columnDefinition = "TEXT")
    private String  errorMessage;

    @CreationTimestamp
    @Column(name= "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "expires_at")
    private Instant expiresAt;


    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Refund> refunds = new ArrayList<>();

    public BigDecimal totalRefunded(){
        return refunds.stream()
                .filter(r->r.getStatus()== RefundStatus.SUCCESS)
                .map(Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


}
