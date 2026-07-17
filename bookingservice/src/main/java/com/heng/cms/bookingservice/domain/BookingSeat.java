package com.heng.cms.bookingservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="booking_seats")
public class BookingSeat{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;
    private UUID showtimeSeatId;
    private String seatLabel;
    private BigDecimal price;
    private Instant createdAt;
    private Instant lastUpdatedAt;
}