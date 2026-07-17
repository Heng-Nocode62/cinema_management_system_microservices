package com.heng.cms.bookingservice.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

public record CreateBookingRequest (
	
	@NotNull(message = "showtime id cannot be null")
	UUID showtimeId,
	
	@NotEmpty(message = "showtime seat id cannot be empty")
	List<UUID> showtimeSeatIds,
	@NotNull(message = "combos must not null")
	List<Item> combos,
	@NotNull(message = "items must not null")
	List<Item> items

){
	public record Item(UUID id, Integer quantity) {

	}
}
