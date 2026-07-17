package com.heng.cms.schedulingservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
public class GetShowtimeSeatsRequest {
    private UUID showtimeId;
    private List<UUID> seatIds;
}
