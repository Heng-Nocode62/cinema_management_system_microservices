package com.heng.cms.cinemaservice.service.impl;

import com.heng.cms.cinemaservice.domain.Screen;
import com.heng.cms.cinemaservice.domain.Seat;
import com.heng.cms.cinemaservice.dto.SeatResponse;
import com.heng.cms.cinemaservice.exception.ResourceNotFoundException;
import com.heng.cms.cinemaservice.repository.ScreenRepository;
import com.heng.cms.cinemaservice.repository.SeatRepository;
import com.heng.cms.cinemaservice.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SeatServiceImpl implements SeatService {
	private final SeatRepository seatRepository;
	private final ScreenRepository screenRepository;
	
	@Override
	public List<SeatResponse> getAllByScreenId(UUID screenId) {
		Screen screen = screenRepository.findById(screenId).orElseThrow(()-> new ResourceNotFoundException("screen",screenId));
		return screen.getSeats().stream().map(seat ->
								SeatResponse.builder()
								.id(seat.getId())
								.label(seat.getRowLabel()+seat.getSeatNumber())
								.seatNumber(seat.getSeatNumber())
								.seatType(seat.getSeatType())
								.groupCoupleId(seat.getCoupleGroupId())
								.build())
				.toList();
	}

	@Override
	public SeatResponse getById(UUID id) {
		Seat seat = seatRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("seat",id));
		return SeatResponse.builder()
				.id(seat.getId())
				.seatNumber(seat.getSeatNumber())
				.seatType(seat.getSeatType())
				.groupCoupleId(seat.getCoupleGroupId())
				.label(seat.getRowLabel())
				.build();
	}

	// TODO getAvailibility seat

}
