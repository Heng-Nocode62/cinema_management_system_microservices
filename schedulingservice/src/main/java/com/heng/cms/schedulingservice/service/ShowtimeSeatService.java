package com.heng.cms.schedulingservice.service;

import com.heng.cms.schedulingservice.dto.GetShowtimeSeatsRequest;
import com.heng.cms.schedulingservice.dto.SeatLockingRequest;
import com.heng.cms.schedulingservice.dto.ShowtimeSeatResponse;
import com.heng.cms.schedulingservice.dto.UpdateSeatStatusRequest;

import java.util.List;
import java.util.UUID;

public interface ShowtimeSeatService {
    List<ShowtimeSeatResponse> getScreenIdAndSeatIds(GetShowtimeSeatsRequest request);

    void updateSeatsStatus(UpdateSeatStatusRequest request);

    ShowtimeSeatResponse getById(UUID showtimeSeatId);

    void lockSeats(SeatLockingRequest request);

    void unlockSeats(SeatLockingRequest request);
}
