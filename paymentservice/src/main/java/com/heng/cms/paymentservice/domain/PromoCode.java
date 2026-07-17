package com.heng.cms.paymentservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "promo_codes")
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(name = "discount_type",nullable = false,length = 10)
    private String discountType;

    @Column(name = "discount_value",nullable = false, precision = 8, scale = 2)
    private BigDecimal discountValue;

    @Column(name = "max_uses")
    private Integer maxUses;

    @Column(name = "uses_count", nullable = false)
    @Builder.Default
    private Integer usesCount = 0;

    @Column(name = "valid_until")
    private LocalDate validUntil;

    @Column(name = "is_active",nullable = false)
    @Builder.Default
    private boolean active = true;
}
