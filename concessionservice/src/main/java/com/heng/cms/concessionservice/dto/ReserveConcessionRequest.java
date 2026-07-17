package com.heng.cms.concessionservice.dto;

import java.awt.*;
import java.util.List;
import java.util.UUID;

public record ReserveConcessionRequest(
        UUID cinemaId,
        List<Item> combos,
        List<Item> items
) {
    public record Item(UUID id, Integer quantity) {}
}
