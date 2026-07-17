package com.heng.cms.cinemaservice.dto;

import com.heng.cms.cinemaservice.domain.enumeric.SeatType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateScreenRequest {
    private String name;
    private String description;
}
