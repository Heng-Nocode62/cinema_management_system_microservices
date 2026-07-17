package com.heng.cms.cinemaservice.service;


import com.heng.cms.cinemaservice.dto.CinemaRequest;
import com.heng.cms.cinemaservice.dto.CinemaResponse;
import com.heng.cms.cinemaservice.dto.CinemaUpdateRequest;
import com.heng.cms.cinemaservice.dto.PageResponse;

import java.util.List;
import java.util.UUID;

public interface CinemaService {

	
	CinemaResponse create(CinemaRequest request);

	PageResponse<CinemaResponse> getAll(int page, int size);

	CinemaResponse getById(UUID id);


	void update(UUID id, CinemaUpdateRequest request);

    List<CinemaResponse> getAllByIds(List<UUID> cinemaIds);
}
