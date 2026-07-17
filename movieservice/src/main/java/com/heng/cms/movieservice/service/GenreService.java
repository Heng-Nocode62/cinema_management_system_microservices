package com.heng.cms.movieservice.service;


import com.heng.cms.movieservice.dto.GenreRequest;
import com.heng.cms.movieservice.dto.GenreResponse;
import com.heng.cms.movieservice.dto.PageResponse;

import java.util.UUID;

public interface GenreService {

	
	PageResponse<GenreResponse> getAll(int page, int size);

	
	GenreResponse createGenre(GenreRequest genreRequest);

	void updateGenre(UUID id, GenreRequest genreDto);


	void deleteGenre(UUID id);


	GenreResponse getById(UUID id);
	

}
