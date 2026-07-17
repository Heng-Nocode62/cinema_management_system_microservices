package com.heng.cms.schedulingservice.dto.client;

import com.heng.cms.schedulingservice.dto.client.enumeric.MovieStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.Set;
import java.util.UUID;

@Getter
@Builder
public class MovieResponse {
	private UUID id;
    private String title;
    private String description;
    private Integer durationMinutes;
    private String ageRating;
    private Set<UUID> genreIds;
    private String posterUrl;
    private String trailerUrl;
    private MovieStatus status;

}
