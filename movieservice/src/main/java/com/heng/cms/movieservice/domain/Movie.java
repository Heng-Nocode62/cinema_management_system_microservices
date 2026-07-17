package com.heng.cms.movieservice.domain;

import com.heng.cms.movieservice.domain.enumeric.MovieStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(name = "title",nullable = false)
    private String title;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;

    @Column(name = "language",nullable = false)
    private String language;

    @Column(name = "country",nullable = false)
    private String country;
    @Column(name = "age_rating",nullable = false)
    private String ageRating;
    @Column(name = "poster_url",nullable = false)
    private String posterUrl;
    @Column(name = "trailer_url", nullable = false)
    private String trailerUrl;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MovieStatus status;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

}
