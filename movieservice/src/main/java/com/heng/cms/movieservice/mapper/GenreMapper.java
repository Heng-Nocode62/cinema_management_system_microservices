package com.heng.cms.movieservice.mapper;

import com.heng.cms.movieservice.domain.Genre;
import com.heng.cms.movieservice.dto.GenreRequest;
import com.heng.cms.movieservice.dto.GenreResponse;
import org.springframework.stereotype.Service;
@Service
public class GenreMapper {
	public Genre toGenre(GenreRequest response) {

		return Genre.builder()
				.name(response.getName())
				.build();
	}
	public GenreRequest toGenreDto(Genre genre) {
		return GenreRequest
				.builder()
				.name(genre.getName())
				.build();
	}
	public GenreResponse toGenreResponse(Genre genre) {
		GenreResponse genreResponse = new GenreResponse();
		genreResponse.setId(genre.getId());
		genreResponse.setName(genre.getName());
		return genreResponse;
	}

}
