package com.heng.cms.cinemaservice.service;


import com.heng.cms.cinemaservice.dto.PageResponse;
import com.heng.cms.cinemaservice.dto.ScreenRequest;
import com.heng.cms.cinemaservice.dto.ScreenResponse;

import java.util.UUID;

public interface ScreenService {

	ScreenResponse create(ScreenRequest request);
	ScreenResponse getById(UUID id);
	PageResponse<ScreenResponse> getAll(int page, int size);

}
