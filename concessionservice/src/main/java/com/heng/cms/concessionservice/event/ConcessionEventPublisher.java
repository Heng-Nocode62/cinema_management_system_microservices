// event/ConcessionEventPublisher.java
package com.heng.cms.concessionservice.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Component @RequiredArgsConstructor @Slf4j
public class ConcessionEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishLowInventory(UUID itemId, UUID cinemaId, int quantity) {
        kafkaTemplate.send("concession.low-inventory", itemId.toString(), Map.of(
                "eventType",  "LOW_INVENTORY",
                "occurredAt", Instant.now().toString(),
                "menuItemId",     itemId.toString(),
                "cinemaId",   cinemaId.toString(),
                "availableQuantity",   quantity
        ));
    }
}