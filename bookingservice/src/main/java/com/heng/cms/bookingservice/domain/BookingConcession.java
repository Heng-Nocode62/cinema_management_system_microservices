package com.heng.cms.bookingservice.domain;


import com.heng.cms.bookingservice.domain.enumeric.ConcessionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "booking_concessions")
public class BookingConcession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "booking_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Booking booking;
    @Column(name = "concession_id", nullable = false)
    private UUID concessionId;
    private String name;
    @Column(name = "unite_price")
    private BigDecimal unitPrice;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private ConcessionType type;
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
}
