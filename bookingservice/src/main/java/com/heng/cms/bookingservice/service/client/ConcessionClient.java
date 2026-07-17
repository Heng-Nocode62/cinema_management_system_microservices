package com.heng.cms.bookingservice.service.client;

import com.heng.cms.bookingservice.domain.BookingConcession;
import com.heng.cms.bookingservice.dto.CreateBookingRequest;
import com.heng.cms.bookingservice.dto.client.ConcessionRequest;
import com.heng.cms.bookingservice.dto.client.ConcessionResponse;
import com.heng.cms.bookingservice.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Slf4j
@Component
public class ConcessionClient {

    @Autowired
    @Qualifier("concessionRestClient")
    private RestClient concessionRestClient;


    public ConcessionResponse reserveConcession(
            UUID cinemaId,
            List<CreateBookingRequest.Item> combos,
            List<CreateBookingRequest.Item> items
    ) {

       return concessionRestClient.post()
                .uri("/api/v1/concessions/reserve")
                .body(buildReserveConcessionRequest(cinemaId, combos, items))
                .retrieve()
               .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .body(ConcessionResponse.class);
    }

    public void confirmConcession(UUID cinemaId,List<BookingConcession> bookingConcessions) {

        concessionRestClient.post()
                .uri("/api/v1/concessions/confirm")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(buildConcessionRequestByBookingConcessions(cinemaId, bookingConcessions))
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .toBodilessEntity();

    }
    public void cancelConcession(UUID cinemaId,List<BookingConcession> bookingConcessions) {

        concessionRestClient.post()
                .uri("/api/v1/concessions/cancel")
                .body(buildConcessionRequestByBookingConcessions(cinemaId, bookingConcessions))
                .retrieve()
                .onStatus(HttpStatusCode::isError,(request, response) -> ClientUtil.handleError(response))
                .toBodilessEntity();

    }

    private Map<String, Object> buildReserveConcessionRequest(
            UUID cinemaId,
            List<CreateBookingRequest.Item> combos,
            List<CreateBookingRequest.Item> items
    ) {
        return Map.of(
                "cinemaId", cinemaId,
                "combos", combos,
                "items", items);
    }

    private Map<String, Object> buildConcessionRequestByBookingConcessions(UUID cinemaId,List<BookingConcession> bookingConcessions) {
        List<ConcessionRequest.Item> items = new ArrayList<>();
        for (BookingConcession bookingConcession : bookingConcessions) {
            ConcessionRequest.Item request = new ConcessionRequest.Item(
                    bookingConcession.getConcessionId(),
                    bookingConcession.getQuantity(),
                    bookingConcession.getType()
            );
            items.add(request);
        }

        return Map.of(
                "cinemaId", cinemaId,
                "items", items
        );
    }



}
