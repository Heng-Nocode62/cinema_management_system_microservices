package com.heng.cms.schedulingservice.mapper;

import com.heng.cms.schedulingservice.domain.ShowtimeSeat;
import com.heng.cms.schedulingservice.dto.ShowtimeSeatResponse;
import com.heng.cms.schedulingservice.dto.client.SeatResponse;
import com.heng.cms.schedulingservice.service.client.CinemaClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowtimeSeatMapper {
    private final CinemaClientService cinemaClientService;

    public ShowtimeSeatResponse toShowtimeSeatResponse(ShowtimeSeat seat){
       SeatResponse seatResponse= cinemaClientService.findSeatBySeatId(seat.getSeatId());
        return ShowtimeSeatResponse.builder()
                .seatId(seat.getId())
                .price(seat.getPrice())
                .seatLabel(seat.getSeatLabel())
                .status(seat.getStatus())
                .groupCoupleId(seatResponse.getGroupCoupleId())
                .build();
    }
}
