package com.heng.cms.cinemaservice.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CinemaResponse {
	private UUID id;
	private String name;
	private String location;
	private String city;
	private String phone;
	private String email;
}
