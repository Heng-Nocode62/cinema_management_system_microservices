package com.heng.cms.schedulingservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class ShowtimeResponse {

    private UUID id;
    private String movieTitle;
    private UUID movieId;
    private String screenName;
    private UUID screenId; // TODO
    private UUID cinemaId;
    private Instant startTime;
    private Instant endTime;
    private BigDecimal basePrice;
    private int totalSeats;
    private int availableSeats;
}
