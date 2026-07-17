package com.heng.cms.movieservice.service.impl;

import com.heng.cms.movieservice.domain.Genre;
import com.heng.cms.movieservice.dto.GenreRequest;
import com.heng.cms.movieservice.dto.GenreResponse;
import com.heng.cms.movieservice.dto.PageResponse;
import com.heng.cms.movieservice.exception.BadRequestException;
import com.heng.cms.movieservice.exception.ResourceNotFoundException;
import com.heng.cms.movieservice.mapper.GenreMapper;
import com.heng.cms.movieservice.repository.GenreRepository;
import com.heng.cms.movieservice.service.GenreService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {
	private final GenreRepository genreRepository;
	private final GenreMapper genreMapper;

	private final Clock clock;



	@Override
	public GenreResponse createGenre(GenreRequest request) {
		Instant now = clock.instant();

		// trime the space outside
		String name = request.getName().trim();

		//validate if exists
		genreRepository.findByName(name).ifPresent(genre ->
		{throw new BadRequestException("Genre already exists");});

		Genre genre = new Genre();
		genre.setName(name);
		genre.setDeleted(false);
		genre.setCreatedAt(now);
		genre.setLastUpdatedAt(now);
		Genre savedGenre = genreRepository.save(genre);
		log.info("Created genre with id {}", savedGenre.getId());
		return genreMapper.toGenreResponse(genre);


	}

	
	@Override
	public PageResponse<GenreResponse> getAll(int page, int size) {
	Pageable pageable = PageRequest.of(page, size);
	Page<Genre> genrePage = genreRepository.findByDeletedFalse(pageable);
	
	List<GenreResponse> genres = genrePage.stream().map(genreMapper::toGenreResponse).toList();
		return new PageResponse<>(genres, genrePage);
	}



	@Override
	@Transactional
	public void updateGenre(UUID id, GenreRequest genreDto) {
		Genre genre = genreRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("genre", id));
		genre.setName(genreDto.getName());
		genre.setLastUpdatedAt(clock.instant());
		log.info("Updated genre with id {}", genre.getId());
	}

	@Override
	@Transactional
	public void deleteGenre(UUID id) {
		
		Genre genre = genreRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new ResourceNotFoundException("genre", id));
		genre.setDeleted(true);
		genre.setLastUpdatedAt(clock.instant());
		log.info("Deleted genre with id {}", genre.getId());
		
	}
	@Override
	public GenreResponse getById(UUID id) {
		Genre genre = genreRepository.findByIdAndDeletedFalse(id).orElseThrow(()-> new ResourceNotFoundException("genre", id));
		return genreMapper.toGenreResponse(genre);
	}

}
