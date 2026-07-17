package com.heng.cms.bookingservice.domain;

import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "bookings")
public class Booking{

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
	
	@Column(nullable = false)
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private UUID showtimeId;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    @Builder.Default
    private List<BookingConcession> bookingConcessions = new ArrayList<>();

    @Column(nullable = false,name = "user_account_id")
    private UUID userAccountId;

    private Instant expiresAt;
    private Instant createdAt;
    private Instant lastUpdatedAt;

}
