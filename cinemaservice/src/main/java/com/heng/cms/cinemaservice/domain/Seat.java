package com.heng.cms.cinemaservice.domain;

import com.heng.cms.cinemaservice.domain.enumeric.SeatType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"screen_id", "rowLabel", "seatNumber"}
        )
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="row_label",nullable = false)
    private String rowLabel;

    @Column(name = "seat_number",nullable = false)
    private Integer seatNumber;

    @Enumerated( EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @Column(name = "couple_group_id")
    private Long coupleGroupId;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;
}
