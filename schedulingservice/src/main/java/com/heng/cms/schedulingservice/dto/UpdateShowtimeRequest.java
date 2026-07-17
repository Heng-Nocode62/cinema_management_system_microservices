package com.heng.cms.schedulingservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
public class UpdateShowtimeRequest {

    private Instant startTime;
    private BigDecimal basePrice;
}