package com.heng.cms.schedulingservice.service.client;

import com.heng.cms.schedulingservice.dto.client.ScreenResponse;
import com.heng.cms.schedulingservice.dto.client.SeatResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

@Component

public class CinemaClientService {
    private final RestClient cinemaRestClient;
    public CinemaClientService(@Qualifier("cinemaRestClient") RestClient restClient) {
        this.cinemaRestClient = restClient;
    }
    /* TODO
    when it comes to authentication, token must be provided
     */
    public ScreenResponse findScreenByScreenId(UUID screenId) {
        return cinemaRestClient.get()
                .uri("/api/v1/screens/{screenId}", screenId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError,((request, response) ->{
                    throw new BadRequestException(readErrorBody(response));
                }))
                .toEntity(ScreenResponse.class)
                .getBody();
    }

    public List<SeatResponse> findSeatsByScreenId(UUID screenId) {
        return cinemaRestClient.get()
                .uri("/api/v1/seats/screen/{screenId}",screenId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) ->
                {
                    throw new BadRequestException(readErrorBody(response));
                })
                .body(new ParameterizedTypeReference<List<SeatResponse>>() {});
    }

    public SeatResponse findSeatBySeatId(UUID seatId) {
        return cinemaRestClient.get()
                .uri("/api/v1/seats/{seatId}", seatId)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) ->{
                    throw new BadRequestException(readErrorBody(response));
                })
                .body(SeatResponse.class);
    }

    private String readErrorBody(ClientHttpResponse response) throws IOException {
        return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8) ;
    }

}
