package com.heng.cms.bookingservice.service.client;

import com.heng.cms.bookingservice.dto.client.ShowtimeResponse;
import com.heng.cms.bookingservice.dto.client.ShowtimeSeatResponse;

import com.heng.cms.bookingservice.dto.client.enumeric.SeatStatus;
import com.heng.cms.bookingservice.exception.BadRequestException;
import com.heng.cms.bookingservice.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class SchedulingClient {
    @Autowired
    @Qualifier("schedulingRestClient")
    private  RestClient schedulingRestClient;
    public List<ShowtimeSeatResponse> findShowtimeSeatByIdsAndShowtimeId(List<UUID> ids, UUID showtimeId) {
        return schedulingRestClient.post()
                .uri("/api/v1/showtime-seat/showtime")
                .body(buildBodyGetShowtimeSeatsRequest(ids,showtimeId))
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .body(new ParameterizedTypeReference<List<ShowtimeSeatResponse>>() {});
    }



    public void updateShowtimeSeatStatus(List<UUID> showtimeSeatIds,SeatStatus seatStatus) {
        schedulingRestClient.put().uri("/api/v1/showtime-seat/update/status")
                .body(buildUpdateSeatStatusRequest(showtimeSeatIds,seatStatus))
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .toBodilessEntity();
    }
    public void lockSeat(List<UUID> showtimeSeatIds) {
        schedulingRestClient.put().uri("/api/v1/showtime-seat/lock")
                .body(Map.of("seatIds", showtimeSeatIds))
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .toBodilessEntity();
    }
    public void unlockSeat(List<UUID> showtimeSeatIds) {
        schedulingRestClient.put().uri("/api/v1/showtime-seat/unlock")
                .body(Map.of("seatIds", showtimeSeatIds))
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .toBodilessEntity();
    }

    public ShowtimeResponse findShowtimeById(UUID showtimeId){
        return schedulingRestClient.get()
                .uri("/api/v1/showtimes/{showtimeId}",showtimeId)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .body(ShowtimeResponse.class);
    }


    private Map<String,Object> buildUpdateSeatStatusRequest(List<UUID> showtimeSeatIds,SeatStatus seatStatus) {
        return Map.of(
                "showtimeSeatIds", showtimeSeatIds,
                "status", seatStatus
        );
    }


    private Map<String, Object> buildBodyGetShowtimeSeatsRequest(List<UUID> ids, UUID showtimeId) {
        return Map.of(
                "seatIds", ids,
                "showtimeId", showtimeId
        );
    }


    private String readErrorBody(ClientHttpResponse response) throws IOException {
        return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
    }



}
