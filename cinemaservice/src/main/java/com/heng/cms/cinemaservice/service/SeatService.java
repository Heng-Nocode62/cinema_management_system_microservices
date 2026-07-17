package com.heng.cms.cinemaservice.service;


import com.heng.cms.cinemaservice.dto.SeatResponse;

import java.util.List;
import java.util.UUID;

public interface SeatService {

	List<SeatResponse> getAllByScreenId(UUID screenId);

    SeatResponse getById(UUID id);
}
