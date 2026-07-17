package com.heng.cms.movieservice.service.impl;

import com.heng.cms.movieservice.domain.Genre;
import com.heng.cms.movieservice.domain.Movie;
import com.heng.cms.movieservice.domain.enumeric.MovieStatus;
import com.heng.cms.movieservice.dto.MovieRequest;
import com.heng.cms.movieservice.dto.MovieResponse;
import com.heng.cms.movieservice.dto.MovieUpdateRequest;
import com.heng.cms.movieservice.dto.PageResponse;
import com.heng.cms.movieservice.exception.BadRequestException;
import com.heng.cms.movieservice.exception.ResourceNotFoundException;
import com.heng.cms.movieservice.mapper.MovieMapper;
import com.heng.cms.movieservice.repository.GenreRepository;
import com.heng.cms.movieservice.repository.MovieRepository;
import com.heng.cms.movieservice.service.MovieService;
import com.heng.cms.movieservice.spec.MovieFilter;
import com.heng.cms.movieservice.spec.MovieSpecification;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;


@Slf4j
@RequiredArgsConstructor
@Service
public class MovieServiceImpl implements MovieService {
	private final MovieRepository movieRepository;
	private final MovieMapper movieMapper;
	private final FileStorageService fileStorageService;
	private final GenreRepository genreRepository;
	private final MovieSpecification movieSpecification;
	private final Clock clock;

	@Override
	public MovieResponse create(MovieRequest request) {

		Movie movie = movieMapper.toMovie(request);
		if(request.getGenreIds() != null && !request.getGenreIds().isEmpty()) {
			Set<Genre> genre = new HashSet<Genre>(genreRepository.findAllById(request.getGenreIds()));
			if (genre.size() == request.getGenreIds().size()) {
				movie.setGenres(genre);
			}else  {
				throw new BadRequestException("Genres not found.");
			}
		}else {
			throw new IllegalStateException("Movie must has at least one genre");
		}

		movieRepository.findByTitle(movie.getTitle().trim()).ifPresent(
				m->{
					if (m.getStatus() != MovieStatus.ARCHIVED){
						throw new BadRequestException("Movie already exists");
					}
				}
		);
		Instant now = clock.instant();
		movie.setCreatedAt(now);
		movie.setLastUpdatedAt(now);
		Movie savedMovie = movieRepository.save(movie);
		log.info("created movie with id {}", savedMovie.getId());
		return movieMapper.toMovieResponse(savedMovie);
	}

	@Override
	public MovieResponse getById(UUID id) {
		Movie movie = movieRepository.findById(id).map(m->{
			if(m.getStatus().equals(MovieStatus.ARCHIVED)) {
				throw new ResourceNotFoundException("Movie with id " + id + " not found");
			}
			return m;
		}).orElseThrow(() -> new ResourceNotFoundException("Movie with id " + id + " not found"));

		return movieMapper.toMovieResponse(movie);
	}


	@Override
	public PageResponse<MovieResponse> getAll(MovieFilter movie, int page, int size) {

		Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

		Specification<Movie> spec = movieSpecification.filter(movie);

		Page<Movie> movies = movieRepository.findAll(spec, pageable);

		List<MovieResponse> movieResponses = movies
				.stream()
				//TODO consider make other api for admin
				.filter(m->!m.getStatus().equals(MovieStatus.ARCHIVED))
				.map(movieMapper::toMovieResponse)
				.toList();

		return new PageResponse<>(movieResponses, movies);
	}



	@Override
	@Transactional
	public MovieResponse updateById(UUID id, MovieUpdateRequest request) {
		Movie movie = movieRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("movie not found"));
		if (StringUtils.hasText(request.getTitle())) movie.setTitle(request.getTitle());
	    if(StringUtils.hasText(request.getDescription())) movie.setDescription(request.getDescription());
	    if(request.getDurationMinutes() != null && request.getDurationMinutes()  >0) movie.setDurationMinutes(request.getDurationMinutes());
	    if (request.getReleaseDate() != null) movie.setReleaseDate(request.getReleaseDate());
	    if (StringUtils.hasText(request.getLanguage())) movie.setLanguage(request.getLanguage());
	    if(StringUtils.hasText(request.getCountry())) movie.setCountry(request.getCountry());
	    if(StringUtils.hasText(request.getAgeRating())) movie.setAgeRating(request.getAgeRating());
	    if(StringUtils.hasText(request.getPosterUrl())) movie.setPosterUrl(request.getPosterUrl());
	    if (StringUtils.hasText(request.getTrailerUrl())) movie.setTrailerUrl(request.getTrailerUrl());
	    if(request.getStatus() != null) movie.setStatus(request.getStatus());
	    
	    if(request.getGenreIds() != null) {
	    	Set<Genre> genres = new HashSet<>( genreRepository.findAllById(request.getGenreIds()));
	    	if(genres.size() != request.getGenreIds().size()) {
	    		throw new ResourceNotFoundException("Some genres not found");
	    	}
	    	movie.setGenres(genres);
	    }

		
		return movieMapper.toMovieResponse(movie);
	}
	
	@Override
	@Transactional
	public void archiveMovie(UUID id) {
		// TODO check when the show repository is done
//		if (showtimeRepository.existsByMovieIdAndStatus(id, SCHEDULED)) {
//		    throw new IllegalStateException("Cannot delete movie with active showtimes");
//		}
		Movie movie = movieRepository.findById(id).orElseThrow(()-> new EntityNotFoundException("movie not found"));
		movie.setStatus(MovieStatus.ARCHIVED);
	}

	@Override
	public String uploadPoster(MultipartFile file) {

		return fileStorageService.storeFile(file);
	}

}
