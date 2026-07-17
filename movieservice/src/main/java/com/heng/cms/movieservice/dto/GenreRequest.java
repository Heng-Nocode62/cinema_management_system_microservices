package com.heng.cms.movieservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenreRequest {
	@NotBlank(message = " genre name is mandatory.")
	private String name;

}
