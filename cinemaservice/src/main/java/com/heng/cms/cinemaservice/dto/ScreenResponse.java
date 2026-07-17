package com.heng.cms.cinemaservice.dto;


import com.heng.cms.cinemaservice.domain.enumeric.ScreenType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class ScreenResponse {
	private UUID id;
	private String name;
	private Integer totalSeats;
	private ScreenType screenType;
	private CinemaResponse cinemaResponse;

}
