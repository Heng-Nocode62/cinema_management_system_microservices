package com.heng.cms.movieservice.mapper;


import com.heng.cms.movieservice.domain.Genre;
import com.heng.cms.movieservice.domain.Movie;
import com.heng.cms.movieservice.dto.MovieRequest;
import com.heng.cms.movieservice.dto.MovieResponse;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MovieMapper {
	public Movie toMovie(MovieRequest req) {
		return Movie.builder()
				.title(req.getTitle())
				.description(req.getDescription())
				.durationMinutes(req.getDurationMinutes())
				.releaseDate(req.getReleaseDate())
				.country(req.getCountry())
				.ageRating(req.getAgeRating())
				.posterUrl(req.getPosterUrl())
				.trailerUrl(req.getTrailerUrl())
				.posterUrl(req.getPosterUrl())
				.status(req.getStatus())
				.build();
	}
	public MovieResponse toMovieResponse(Movie movie) {
		return MovieResponse.builder()
				.id(movie.getId())
				.title(movie.getTitle())
				.description(movie.getDescription())
				.durationMinutes(movie.getDurationMinutes())
				.ageRating(movie.getAgeRating())
				.genreIds(movie.getGenres().stream().map(Genre::getId).collect(Collectors.toSet()))
				.posterUrl(movie.getPosterUrl())
				.trailerUrl(movie.getTrailerUrl())
				.status(movie.getStatus())
				.build();
	}

}
