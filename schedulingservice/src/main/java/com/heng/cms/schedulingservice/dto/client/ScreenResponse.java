package com.heng.cms.schedulingservice.dto.client;


import com.heng.cms.schedulingservice.dto.client.enumeric.ScreenType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Builder
public class ScreenResponse {
	private UUID id;
	private String name;
	private Integer totalSeats;
	private ScreenType screenType;
	private CinemaResponse cinemaResponse;

}
