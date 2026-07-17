package com.heng.cms.schedulingservice.domain;

import com.heng.cms.schedulingservice.domain.enumeric.SeatStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "showtime_seats",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {"showtime_id", "seat_id"}
    )
)
public class ShowtimeSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Showtime showtime;

    private UUID seatId;
    private String seatLabel;

    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    private Instant createdAt;
    private Instant lastUpdatedAt;
}
