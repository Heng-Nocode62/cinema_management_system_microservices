package com.heng.cms.schedulingservice.service;


import com.heng.cms.schedulingservice.dto.*;

import java.util.List;
import java.util.UUID;

public interface ShowtimeService {

	ShowtimeResponse create(CreateShowtimeRequest request);
	PageResponse<ShowtimeResponse> getAllShowtime(int page, int size);
	ShowtimeResponse getShowtimeById(UUID id);
	void cancelShowtime(UUID id);
	ShowtimeResponse updateShowtime(UUID showtimeId, UpdateShowtimeRequest request);
	void completeFinishedShowtime();

	List<ShowtimeSeatResponse> getShowtimeSeatsByShowtimeId(UUID id);


	//TODO Filter showtime
}
