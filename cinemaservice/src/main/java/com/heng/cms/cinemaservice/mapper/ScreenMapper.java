package com.heng.cms.cinemaservice.mapper;

import com.heng.cms.cinemaservice.domain.Screen;
import com.heng.cms.cinemaservice.dto.ScreenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScreenMapper {
    private final CinemaMapper cinemaMapper;

    public ScreenResponse toScreenResponse(Screen screen) {
        return ScreenResponse.builder()
                .id(screen.getId())
                .name(screen.getName())
                .screenType(screen.getScreenType())
                .cinemaResponse(cinemaMapper.toCinemaResponse(screen.getCinema()))
                .totalSeats(screen.getTotalSeats())
                .build();
    }
}
