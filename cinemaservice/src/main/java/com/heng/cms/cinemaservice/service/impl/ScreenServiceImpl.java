package com.heng.cms.cinemaservice.service.impl;

import com.heng.cms.cinemaservice.domain.Cinema;
import com.heng.cms.cinemaservice.domain.Screen;
import com.heng.cms.cinemaservice.domain.Seat;
import com.heng.cms.cinemaservice.domain.enumeric.SeatType;
import com.heng.cms.cinemaservice.dto.PageResponse;
import com.heng.cms.cinemaservice.dto.ScreenRequest;
import com.heng.cms.cinemaservice.dto.ScreenResponse;
import com.heng.cms.cinemaservice.exception.BadRequestException;
import com.heng.cms.cinemaservice.exception.ResourceNotFoundException;
import com.heng.cms.cinemaservice.mapper.CinemaMapper;
import com.heng.cms.cinemaservice.mapper.ScreenMapper;
import com.heng.cms.cinemaservice.repository.CinemaRepository;
import com.heng.cms.cinemaservice.repository.ScreenRepository;
import com.heng.cms.cinemaservice.repository.SeatRepository;
import com.heng.cms.cinemaservice.service.ScreenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ScreenServiceImpl implements ScreenService {
	private final ScreenRepository screenRepository;
	private final SeatRepository seatRepository;
	private final CinemaRepository cinemaRepository;
	private final CinemaMapper cinemaMapper;
	private final ScreenMapper screenMapper;
	private final Clock clock;
	
	
	@Override
	@Transactional
	public ScreenResponse create(ScreenRequest request) {

		Cinema cinema = cinemaRepository.findById(request.getCinemaId())
				.orElseThrow(()-> new ResourceNotFoundException("cinema",request.getCinemaId()));
		cinema.getScreens().forEach(s->{
			if (s.getName().equals(request.getName())) {
				throw new BadRequestException("screen name already exists");
			}
		});
		Instant now = clock.instant();
		Screen screen = new Screen();
		screen.setName(request.getName());
		screen.setScreenType(request.getScreenType());
		screen.setCinema(cinema);
		screen.setTotalSeats(validateAndCalculateTotalSeat(request));
		
		if(!Collections.disjoint(request.getCoupleRows(), request.getVipRows())) {
			throw new IllegalArgumentException("the couple rows and vip rows are overlap");
		}
		screen.setCreatedAt(now);
		screen.setLastUpdatedAt(now);
		
		screen = screenRepository.save(screen);
		
		List<Seat> seats = new ArrayList<>();
		
		long coupleGroupCounter = 1;
		
		for( int row =1; row <= request.getRows(); row++) {
			boolean isCoupleRow = request.getCoupleRows().contains(row);
			boolean isVipRow = request.getVipRows().contains(row);
			
			for(int number = 1; number <= request.getSeatPerRow(); number ++) {
				Seat seat = new Seat();
				seat.setScreen(screen);
				seat.setRowLabel(String.valueOf((char)('A'+row -1)));
				seat.setSeatNumber(number);
				if(isCoupleRow) {
					if(request.getSeatPerRow()%2 != 0 && number == request.getSeatPerRow() ) {
						continue;
					}
					seat.setSeatType(SeatType.COUPLE);
					seat.setCoupleGroupId(coupleGroupCounter);
					if(number % 2 == 0 ) {
						coupleGroupCounter++;
					}
				}else if (isVipRow) {
					seat.setSeatType(SeatType.VIP);
				}else {
					seat.setSeatType(SeatType.REGULAR);
				}
				seat.setCreatedAt(now);
				seat.setLastUpdatedAt(now);
				seats.add(seat);
			}
			
		}
		seatRepository.saveAll(seats);

		log.info("screen with id = {} is created", screen.getId());
		
		
		return screenMapper.toScreenResponse(screen);
	}

	private Integer validateAndCalculateTotalSeat(ScreenRequest request) {
		int totalSeats = request.getSeatPerRow()*request.getRows();
		if(!request.getCoupleRows().isEmpty()) {
			if(request.getSeatPerRow()%2 !=0){
				return totalSeats-request.getCoupleRows().size();
			}
		}
		return totalSeats;
	}


	@Override
	public ScreenResponse getById(UUID id) {
		Screen screen = screenRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("screen not found"));
		return screenMapper.toScreenResponse(screen);
	}
	
	@Override
	//TODO add filter later
	public PageResponse<ScreenResponse> getAll(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Screen> screens = screenRepository.findAll(pageable);
		List<ScreenResponse>  screenResponses = screens.stream().map(screenMapper::toScreenResponse).toList();
		return new PageResponse<>(screenResponses,screens);
	}

}
