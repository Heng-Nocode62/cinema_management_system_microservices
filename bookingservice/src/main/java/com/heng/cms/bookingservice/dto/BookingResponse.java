package com.heng.cms.bookingservice.dto;

import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import com.heng.cms.bookingservice.dto.client.PaymentInitiateResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
public class BookingResponse {
	private UUID bookingId;
	private UUID showtimeId;
	private List<String> seatLabels;
	private BigDecimal totalPrice;
	private BookingStatus status;
	private Instant bookingDate;
	private PaymentInitiateResponse paymentInitiateResponse;

}
