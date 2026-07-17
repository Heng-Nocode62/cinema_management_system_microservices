package com.heng.cms.bookingservice.dto.client;

import com.heng.cms.bookingservice.dto.client.enumeric.SeatStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShowtimeSeatResponse {
    private UUID seatId;
    private BigDecimal price;
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
    private String seatLabel;
    private Long groupCoupleId;
}
