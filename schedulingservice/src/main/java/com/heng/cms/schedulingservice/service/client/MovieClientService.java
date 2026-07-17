package com.heng.cms.schedulingservice.service.client;

import com.heng.cms.schedulingservice.dto.client.MovieResponse;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class MovieClientService {

    private final RestClient movieRestClient;
    public MovieClientService(@Qualifier("movieRestClient") RestClient movieRestClient) {
        this.movieRestClient = movieRestClient;
    }

    /* TODO
   when it comes to authentication, token must be provided
    */
    public MovieResponse findByMovieId(UUID movieId){
        return movieRestClient.get()
                .uri("/api/v1/movies/{movieId}", movieId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatusCode::isError,(req, res)->{
                    String errorBody = readErrorBody(res);
                    throw new BadRequestException(errorBody);
                })
                .toEntity(MovieResponse.class)
                .getBody();
    }

    private String readErrorBody(ClientHttpResponse response) throws IOException {
        return new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8) ;
    }
}
