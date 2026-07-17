package com.heng.cms.schedulingservice.service.impl;

import com.heng.cms.schedulingservice.domain.Showtime;
import com.heng.cms.schedulingservice.domain.ShowtimeSeat;
import com.heng.cms.schedulingservice.domain.enumeric.SeatStatus;
import com.heng.cms.schedulingservice.domain.enumeric.ShowtimeStatus;
import com.heng.cms.schedulingservice.dto.GetShowtimeSeatsRequest;
import com.heng.cms.schedulingservice.dto.SeatLockingRequest;
import com.heng.cms.schedulingservice.dto.ShowtimeSeatResponse;
import com.heng.cms.schedulingservice.dto.UpdateSeatStatusRequest;
import com.heng.cms.schedulingservice.exception.BadRequestException;
import com.heng.cms.schedulingservice.exception.ResourceNotFoundException;
import com.heng.cms.schedulingservice.mapper.ShowtimeSeatMapper;
import com.heng.cms.schedulingservice.repository.ShowtimeRepository;
import com.heng.cms.schedulingservice.repository.ShowtimeSeatRepository;
import com.heng.cms.schedulingservice.service.ShowtimeSeatService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShowtimeSeatServiceImpl implements ShowtimeSeatService {
    private final ShowtimeSeatRepository  showtimeSeatRepository;
    private final ShowtimeRepository showtimeRepository;
    private final ShowtimeSeatMapper showtimeSeatMapper;
    private final Clock clock;

    @Override
    public List<ShowtimeSeatResponse> getScreenIdAndSeatIds(GetShowtimeSeatsRequest request) {
        List<ShowtimeSeat> showtimeSeats = showtimeSeatRepository.findAllById(request.getSeatIds());
        Showtime showtime = showtimeRepository.findByIdAndStatus(request.getShowtimeId(), ShowtimeStatus.SCHEDULED)
                .orElseThrow(()->new BadRequestException("show time not found."));

        if(showtimeSeats.size() != request.getSeatIds().size()){
            throw new BadRequestException("show time seats not found");
        }
       showtimeSeats.forEach(seat -> {
            if (!seat.getShowtime().getId().equals(showtime.getId())) {
                throw new BadRequestException("show time seats not found");
            }
        });

        List<ShowtimeSeatResponse> responses = showtimeSeats.stream()
                .map(showtimeSeatMapper::toShowtimeSeatResponse).toList();
        return responses;
    }

    @Override
    @Transactional
    public void updateSeatsStatus(UpdateSeatStatusRequest request) {

        List<ShowtimeSeat> seat = showtimeSeatRepository.findAllById(request.getShowtimeSeatIds());
        seat.forEach(s->{
            s.setStatus(request.getStatus());
            s.setLastUpdatedAt(clock.instant());
        });

    }

    @Override
    public ShowtimeSeatResponse getById(UUID showtimeSeatId) {
        ShowtimeSeat showtimeSeat= showtimeSeatRepository.findById(showtimeSeatId)
                .orElseThrow(()-> new ResourceNotFoundException("showtime seat", showtimeSeatId));
        return showtimeSeatMapper.toShowtimeSeatResponse(showtimeSeat);
    }

    @Override
    @Transactional
    public void lockSeats(SeatLockingRequest request) {
        List<ShowtimeSeat> seats = showtimeSeatRepository.findAllByIdWithLock(request.getSeatIds());
        seats.forEach(seat -> {
            if(!seat.getStatus().equals(SeatStatus.AVAILABLE)){
                throw new IllegalStateException("seat status is not available");
            }
            seat.setStatus(SeatStatus.RESERVED);
            seat.setLastUpdatedAt(clock.instant());
        });
    }

    @Override
    @Transactional
    public void unlockSeats(SeatLockingRequest request) {
        List<ShowtimeSeat> seats = showtimeSeatRepository.findAllById(request.getSeatIds());
        seats.forEach(seat -> {
            if(!seat.getStatus().equals(SeatStatus.RESERVED)){
                throw new IllegalStateException("seat status is not reserved");
            }
            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setLastUpdatedAt(clock.instant());
        });
    }


}
