package com.heng.cms.cinemaservice.dto;

import com.heng.cms.cinemaservice.domain.enumeric.ScreenType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ScreenRequest {
	@NotBlank(message = "screen name is mandatory.")
	private String name;
	@NotBlank(message = "screen type is mandatory.")
	private ScreenType screenType;
	@NotBlank(message = "cinema id is mandatory.")
	private UUID cinemaId;
	@NotNull(message = "rows is mandatory.")
	@Positive(message = "rows must be positive.")
	private Integer rows;

	@NotNull(message = "rows is mandatory.")
	@Positive(message = "seat per row must be positive.")
	private Integer seatPerRow;
	@NotNull(message = "vip rows must not null")
	private Set<Integer> vipRows;

	@NotNull(message = "couple rows must not null")
	private Set<Integer> coupleRows;

}
