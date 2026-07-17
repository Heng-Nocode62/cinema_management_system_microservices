package com.heng.cms.schedulingservice.dto;

import com.heng.cms.schedulingservice.domain.enumeric.SeatStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UpdateSeatStatusRequest {
    @NotNull
    private List<UUID> showtimeSeatIds;
    @NotNull
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
