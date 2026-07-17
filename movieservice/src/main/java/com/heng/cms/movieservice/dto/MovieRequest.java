package com.heng.cms.movieservice.dto;

import com.heng.cms.movieservice.domain.enumeric.MovieStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MovieRequest {
	@NotBlank(message = "title is mandatory.")
	private String title;
	
	@NotBlank(message = "description is mandatory.")
	private String description;
	
	@NotNull(message = "duration is mandatory.")
	private Integer durationMinutes;
	
	@NotNull(message = "release date is mandatory.")
	private LocalDate releaseDate;
	
	@NotBlank(message = "language is mandatory.")
	private String language;
	
	@NotBlank(message = "country is mandatory")
	private String country;
	
	@NotBlank(message = "age rating is mandatory")
	private String ageRating;
	
	@NotBlank(message = "poster url is mandatory.")
	private String posterUrl;
	
	@NotBlank(message = "trailer url is mandatory.")
	private String trailerUrl;
	
	@NotNull(message = "status is mandatory")
	@Enumerated(EnumType.STRING)
	private MovieStatus status;

	@NotEmpty(message = "genre id cannot be empty")
	@NotNull(message = "genre id is mandatory")
	private Set<UUID> genreIds;

}
