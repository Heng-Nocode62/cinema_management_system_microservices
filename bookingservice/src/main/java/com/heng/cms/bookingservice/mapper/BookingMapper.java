package com.heng.cms.bookingservice.mapper;

import com.heng.cms.bookingservice.domain.Booking;
import com.heng.cms.bookingservice.dto.BookingResponse;
import com.heng.cms.bookingservice.dto.client.PaymentInitiateResponse;
import com.heng.cms.bookingservice.dto.client.ShowtimeSeatResponse;
import com.heng.cms.bookingservice.service.client.SchedulingClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingMapper {
	private final SchedulingClient schedulingClient;

	public BookingResponse toBookingResponse(Booking booking, PaymentInitiateResponse paymentInitiateResponse) {
        List<UUID> seatIds = booking.getBookingSeats().stream().map(s->s.getShowtimeSeatId()).toList();
        List<ShowtimeSeatResponse> seatResponses = schedulingClient.findShowtimeSeatByIdsAndShowtimeId(seatIds, booking.getShowtimeId());
        List<String> seatLabels = seatResponses.stream().map(ShowtimeSeatResponse::getSeatLabel).toList();


		return BookingResponse.builder()
				.bookingId(booking.getId())
				.showtimeId(booking.getShowtimeId())
				.seatLabels(seatLabels)
				.totalPrice(booking.getTotalPrice())
				.status(booking.getStatus())
				.bookingDate(booking.getCreatedAt())
				.paymentInitiateResponse(paymentInitiateResponse)
				.build();
	}

}
