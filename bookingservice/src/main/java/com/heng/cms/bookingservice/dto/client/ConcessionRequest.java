package com.heng.cms.bookingservice.dto.client;

import com.heng.cms.bookingservice.domain.enumeric.ConcessionType;

import java.util.List;
import java.util.UUID;

public record ConcessionRequest(
        UUID cinemaId,
        List<Item> items

) {
    public record Item(UUID id,Integer quantity, ConcessionType type) {}
}