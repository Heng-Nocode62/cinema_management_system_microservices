package com.heng.cms.schedulingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class CreateShowtimeRequest {

    @NotNull(message =" movie id cannot be null")
    private UUID movieId;

    @NotNull(message = "screen id cannot be null")
    private UUID screenId;

    @NotNull(message = "start time cannot be null")
    private Instant startTime;

    @NotNull(message = "base price cannot be null")
    private BigDecimal basePrice;
}
