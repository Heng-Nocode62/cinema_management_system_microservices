package com.heng.cms.bookingservice.service;


import com.heng.cms.bookingservice.dto.BookingResponse;
import com.heng.cms.bookingservice.dto.CreateBookingRequest;
import com.heng.cms.bookingservice.dto.PageResponse;
import com.heng.cms.bookingservice.spec.BookingFilter;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;

public interface BookingService {
	BookingResponse createBooking(CreateBookingRequest request);

	/*find expired bookings
	 * find showtimeSeats from that booking and set to available
	 * set bookings to cancel
	 *
	 * */
	@Transactional
	@Scheduled(fixedRateString = "${app.booking.schedule.fixed-rate-cancel-milliseconds}")
	void releaseExpirationBooking();

    void confirmBooking(UUID bookingId);


//
//	BookingResponse getById(UUID id);
//
//    PageResponse<BookingResponse> getAllBooking(int page, int size, BookingFilter filter);
//
	void cancelBookingById(UUID id);

}
