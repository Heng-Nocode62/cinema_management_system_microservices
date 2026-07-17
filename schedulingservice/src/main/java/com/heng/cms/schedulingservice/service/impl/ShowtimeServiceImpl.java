package com.heng.cms.schedulingservice.service.impl;

import com.heng.cms.schedulingservice.domain.Showtime;
import com.heng.cms.schedulingservice.domain.ShowtimeSeat;
import com.heng.cms.schedulingservice.domain.enumeric.SeatStatus;
import com.heng.cms.schedulingservice.domain.enumeric.ShowtimeStatus;
import com.heng.cms.schedulingservice.dto.*;
import com.heng.cms.schedulingservice.dto.client.MovieResponse;
import com.heng.cms.schedulingservice.dto.client.ScreenResponse;
import com.heng.cms.schedulingservice.dto.client.SeatResponse;
import com.heng.cms.schedulingservice.dto.client.enumeric.SeatType;
import com.heng.cms.schedulingservice.exception.BadRequestException;
import com.heng.cms.schedulingservice.exception.ResourceNotFoundException;
import com.heng.cms.schedulingservice.mapper.ShowtimeMapper;
import com.heng.cms.schedulingservice.mapper.ShowtimeSeatMapper;
import com.heng.cms.schedulingservice.repository.ShowtimeRepository;
import com.heng.cms.schedulingservice.repository.ShowtimeSeatRepository;
import com.heng.cms.schedulingservice.service.ShowtimeService;
import com.heng.cms.schedulingservice.service.client.CinemaClientService;
import com.heng.cms.schedulingservice.service.client.MovieClientService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {
	private final CinemaClientService cinemaClientService;
	private final ShowtimeRepository showtimeRepository;
	private final MovieClientService movieClientService;
	private final ShowtimeSeatRepository showtimeSeatRepository;
	private final ShowtimeMapper showtimeMapper;
	private final ShowtimeSeatMapper showtimeSeatMapper;
	private final Clock clock;

	@Override
	@Transactional
	public ShowtimeResponse create(CreateShowtimeRequest request) {
		Instant now = clock.instant();

		if (request.getStartTime().isBefore(now)) {
			throw new BadRequestException("Start time is before now");
		}

		//Response not understandable
		MovieResponse movie = movieClientService.findByMovieId(request.getMovieId());
		ScreenResponse screen = cinemaClientService.findScreenByScreenId(request.getScreenId());


		Instant endTime = request.getStartTime().plusSeconds(movie.getDurationMinutes()*60);

		boolean isOverlapTime = showtimeRepository.existsOverlappingShowtime(screen.getId(), request.getStartTime(), endTime);
		if (isOverlapTime) {
			throw new BadRequestException("the time of showing is overlap");
		}
		Showtime showtime = Showtime.builder()
				.screenId(screen.getId())
				.movieId(movie.getId())
				.startTime(request.getStartTime())
				.endTime(endTime)
				.status(ShowtimeStatus.SCHEDULED)
				.basePrice(request.getBasePrice())
				.createdAt(now)
				.lastUpdatedAt(now)
				.build();

		Showtime savedShowtime = showtimeRepository.save(showtime);
		generateShowtimeSeats(savedShowtime, screen, now);

		return showtimeMapper.toShowtimeResponse(savedShowtime,movie,screen);
	}

	private void generateShowtimeSeats(Showtime showtime, ScreenResponse screen, Instant now) {

		List<SeatResponse> seats = cinemaClientService.findSeatsByScreenId(screen.getId());
		List<ShowtimeSeat> showtimeSeats = seats.stream().map(seat -> {
			ShowtimeSeat ss = new ShowtimeSeat();
			ss.setSeatId(seat.getId());
			ss.setSeatLabel(seat.getLabel());
			ss.setShowtime(showtime);
			ss.setStatus(SeatStatus.AVAILABLE);
			ss.setPrice(calculateSeatPrice(seat.getSeatType(), showtime.getBasePrice()));
			ss.setCreatedAt(now);
			ss.setLastUpdatedAt(now);
			return ss;
		}).toList();

		showtimeSeatRepository.saveAll(showtimeSeats);

	}

	private BigDecimal calculateSeatPrice(SeatType seatType, BigDecimal basePrice) {
		return switch (seatType) {
		case VIP -> basePrice.multiply(BigDecimal.valueOf(1.5));
		case COUPLE -> basePrice.multiply(BigDecimal.valueOf(1.4));
		default -> basePrice;
		};
	}

	
	@Override
	// TODO add filter
	public PageResponse<ShowtimeResponse> getAllShowtime(int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<Showtime> showtimes = showtimeRepository.findAllScheduledShowtimes(pageable);
		List<ShowtimeResponse> showtimeResponses = showtimes.stream()
				.map(showtime -> {
					MovieResponse movie = movieClientService.findByMovieId(showtime.getMovieId());
					ScreenResponse screen = cinemaClientService.findScreenByScreenId(showtime.getScreenId());
					return showtimeMapper.toShowtimeResponse(showtime, movie, screen);
				})
				.toList();

		return new PageResponse<>(showtimeResponses, showtimes);
	}

	@Override
	//TODO do not show completed showtime
	public ShowtimeResponse getShowtimeById(UUID id) {
		Showtime showtime = showtimeRepository.findByIdAndStatus(id,ShowtimeStatus.SCHEDULED).orElseThrow(()-> new ResourceNotFoundException("showtime", id));
		if (showtime.getStatus()!= ShowtimeStatus.SCHEDULED){
			throw new ResourceNotFoundException("showtime", id);
		}
		return showtimeMapper.toShowtimeResponse(showtime);
	}
	
	@Transactional
	@Override
	//TODO do not show completed showtime
	public ShowtimeResponse updateShowtime(UUID showtimeId, UpdateShowtimeRequest request) {

		Instant now = clock.instant();
	    Showtime showtime = showtimeRepository.findByIdAndStatus(showtimeId,ShowtimeStatus.SCHEDULED)
	            .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

	    // Check if any seat is BOOKED
	    boolean hasBookedSeats = showtimeSeatRepository
	            .existsByShowtimeIdAndStatus(showtimeId, SeatStatus.BOOKED);

	    if (hasBookedSeats) {
	        throw new IllegalStateException("Cannot update showtime because tickets are already booked.");
	    }

	    // Update time if provided
	    if (request.getStartTime() != null ) {
			if (request.getStartTime().isBefore(now)){
				throw new BadRequestException("start time is before now");
			}

			ScreenResponse screen = cinemaClientService.findScreenByScreenId(showtime.getScreenId());

			long durationSecond = showtime.getEndTime().getEpochSecond() -showtime.getStartTime().getEpochSecond();
			Instant endTime = request.getStartTime().plusSeconds(durationSecond);
			boolean isOverlapTime = showtimeRepository.existsOverlappingShowtime(screen.getId(), request.getStartTime(), endTime);
			if (isOverlapTime) {
				throw new BadRequestException("the time of showing is overlap");
			}

	        showtime.setStartTime(request.getStartTime());
			showtime.setEndTime(endTime);
			showtime.setLastUpdatedAt(now);
	    }


	    // Update base price & recalculate seats
	    if (request.getBasePrice() != null) {
	        showtime.setBasePrice(request.getBasePrice());

	        List<ShowtimeSeat> seats =
	                showtimeSeatRepository.findByShowtimeId(showtimeId);

	        for (ShowtimeSeat seat : seats) {

				SeatResponse seatResponse = cinemaClientService.findSeatBySeatId(seat.getSeatId());

	            BigDecimal newPrice = request.getBasePrice();

	            if (seatResponse.getSeatType() == SeatType.VIP) {
	                newPrice = newPrice.multiply(BigDecimal.valueOf(1.5));
	            } else if (seatResponse.getSeatType() == SeatType.COUPLE) {
	                newPrice = newPrice.multiply(BigDecimal.valueOf(2));
	            }

	            seat.setPrice(newPrice);
				seat.setLastUpdatedAt(now);
	        }

	        showtimeSeatRepository.saveAll(seats);
			showtime.setLastUpdatedAt(now);
	    }

	    return showtimeMapper.toShowtimeResponse(showtime);
	}
	
	
	@Transactional
	@Override
	public void cancelShowtime(UUID showtimeId) {
		Instant now = clock.instant();

	    Showtime showtime = showtimeRepository.findByIdAndStatus(showtimeId,ShowtimeStatus.SCHEDULED)
	            .orElseThrow(() -> new ResourceNotFoundException("Showtime not found"));

	    // Cannot delete if already cancelled
	    if (showtime.getStatus() == ShowtimeStatus.CANCELLED) {
	        throw new IllegalStateException("Showtime already cancelled.");
	    }

	    // Cannot delete if showtime already started
	    if (showtime.getStartTime().isBefore(now)) {
	        throw new IllegalStateException("Cannot cancel showtime that already started.");
	    }

	    // Check if any seat is BOOKED
	    boolean hasBookedSeats = showtimeSeatRepository
	            .existsByShowtimeIdAndStatus(showtimeId, SeatStatus.BOOKED);

	    if (hasBookedSeats) {
	        throw new IllegalStateException("Cannot cancel showtime because tickets are already sold.");
	    }

	    // Release RESERVED seats
	    List<ShowtimeSeat> seats =
	            showtimeSeatRepository.findByShowtimeId(showtimeId);

	    for (ShowtimeSeat seat : seats) {
	        if (seat.getStatus() == SeatStatus.RESERVED) {
	            seat.setStatus(SeatStatus.AVAILABLE);
	        }
	    }

	    showtimeSeatRepository.saveAll(seats);

	    //Mark showtime as CANCELLED (Soft Delete)
	    showtime.setStatus(ShowtimeStatus.CANCELLED);

	    showtimeRepository.save(showtime);
	}




	@Override
	public List<ShowtimeSeatResponse> getShowtimeSeatsByShowtimeId(UUID id) {
		Showtime showtime = showtimeRepository.findByIdAndStatus(id, ShowtimeStatus.SCHEDULED)
				.orElseThrow(()-> new ResourceNotFoundException("showtime",id));
		List<ShowtimeSeat> seats = showtimeSeatRepository.findByShowtimeId(showtime.getId());

		return seats.stream().map(showtimeSeatMapper::toShowtimeSeatResponse)
				.toList();
	}

	
	@Override
	@Transactional
	@Scheduled(fixedRate = 60000)
	public void completeFinishedShowtime() {
		List<Showtime> showtimes = showtimeRepository.findFinishedShowtimes(clock.instant(), ShowtimeStatus.SCHEDULED);
		showtimes.forEach(s -> s.setStatus(ShowtimeStatus.COMPLETED));
	}




}
