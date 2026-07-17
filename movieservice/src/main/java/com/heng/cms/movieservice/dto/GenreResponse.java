package com.heng.cms.movieservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class GenreResponse {
	private UUID id;
	private String name;

}
