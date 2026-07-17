package com.heng.cms.concessionservice.dto;

import java.util.List;
import java.util.UUID;

public record ConcessionRequest(
        UUID cinemaId,
        List<Item> items

) {
    public record Item(UUID id,Integer quantity, ConcessionType type) {}
}
