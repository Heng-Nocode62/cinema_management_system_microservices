package com.heng.cms.bookingservice.service.client;

import com.heng.cms.bookingservice.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class ClientUtil {
    public static void handleError(ClientHttpResponse response) throws IOException {
        String body = new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8);
        log.warn("Concession service error: {}", body);
        throw new BadRequestException(body);
    }
}
