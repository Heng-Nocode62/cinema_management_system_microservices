package com.heng.cms.movieservice.dto;

import com.heng.cms.movieservice.domain.enumeric.MovieStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class MovieUpdateRequest {
    private String title;

    private String description;

    private Integer durationMinutes;

    private LocalDate releaseDate;

    private String language;

    private String country;

    private String ageRating;

    private String posterUrl;

    private String trailerUrl;

    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    private Set<UUID> genreIds;

}
