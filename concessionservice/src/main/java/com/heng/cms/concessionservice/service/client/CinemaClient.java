package com.heng.cms.concessionservice.service.client;

import com.heng.cms.concessionservice.exception.CinemaFindingException;
import com.heng.cms.concessionservice.service.client.dto.CinemaResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class CinemaClient {
    private final RestClient cinemaRestClient;

    public CinemaResponse getCinemaById(UUID cinemaId){
        return cinemaRestClient.get()
                .uri("/api/v1/cinemas/"+ cinemaId)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> {
                    String message =readErrorBody(response);
                    log.warn("CANNOT GET CINEMA, message={}",message );
                    throw new CinemaFindingException(message);
                })
                .body(CinemaResponse.class);

    }
    public List<CinemaResponse> getCinemaByIds( List<UUID> cinemaIds){
        if (cinemaIds == null || cinemaIds.isEmpty()){
            return Collections.emptyList();
        }
        return cinemaRestClient.post()
                .uri("/api/v1/cinemas/batch")
                .body(cinemaIds)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> {
                    String message =readErrorBody(response);
                    log.warn("CANNOT GET CINEMAS, message={}",message );
                    throw new CinemaFindingException(message);
                })
                .body(new ParameterizedTypeReference<List<CinemaResponse>>(){});

    }

    private String readErrorBody(ClientHttpResponse response) throws IOException {
        return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8) ;
    }
}
