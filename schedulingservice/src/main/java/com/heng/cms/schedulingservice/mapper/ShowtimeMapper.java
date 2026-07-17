package com.heng.cms.schedulingservice.mapper;

import com.heng.cms.schedulingservice.domain.Showtime;
import com.heng.cms.schedulingservice.domain.enumeric.SeatStatus;
import com.heng.cms.schedulingservice.dto.ShowtimeResponse;
import com.heng.cms.schedulingservice.dto.client.MovieResponse;
import com.heng.cms.schedulingservice.dto.client.ScreenResponse;
import com.heng.cms.schedulingservice.repository.ShowtimeSeatRepository;
import com.heng.cms.schedulingservice.service.client.CinemaClientService;
import com.heng.cms.schedulingservice.service.client.MovieClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ShowtimeMapper {
    private final ShowtimeSeatRepository showtimeSeatRepository;
    private final MovieClientService movieClientService;
    private final CinemaClientService cinemaClientService;

    public ShowtimeResponse toShowtimeResponse(Showtime showtime, MovieResponse movie, ScreenResponse screen) {
        int totalSeat = showtimeSeatRepository.countByShowtimeId(showtime.getId());
        int availableSeats = showtimeSeatRepository.countByShowtimeIdAndStatus(showtime.getId(), SeatStatus.AVAILABLE);
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovieId())
                .movieTitle(movie.getTitle())
                .screenId(showtime.getScreenId())
                .screenName(screen.getName())
                .cinemaId(screen.getCinemaResponse().getId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .basePrice(showtime.getBasePrice())
                .availableSeats(availableSeats)
                .totalSeats(totalSeat).build();
    }
    public ShowtimeResponse toShowtimeResponse(Showtime showtime) {
        ScreenResponse screen = cinemaClientService.findScreenByScreenId(showtime.getScreenId());
        MovieResponse movie = movieClientService.findByMovieId(showtime.getMovieId());


        int totalSeat = showtimeSeatRepository.countByShowtimeId(showtime.getId());
        int availableSeats = showtimeSeatRepository.countByShowtimeIdAndStatus(showtime.getId(), SeatStatus.AVAILABLE);
        return ShowtimeResponse.builder()
                .id(showtime.getId())
                .movieId(showtime.getMovieId())
                .movieTitle(movie.getTitle())
                .screenId(showtime.getScreenId())
                .screenName(screen.getName())
                .cinemaId(screen.getCinemaResponse().getId())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .basePrice(showtime.getBasePrice())
                .availableSeats(availableSeats)
                .totalSeats(totalSeat).build();
    }
}
