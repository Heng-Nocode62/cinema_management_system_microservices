package com.heng.cms.bookingservice.factory;

import com.heng.cms.bookingservice.domain.Booking;
import com.heng.cms.bookingservice.domain.BookingSeat;
import com.heng.cms.bookingservice.dto.client.ShowtimeSeatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BookingSeatFactory {

    public BookingSeat createBookingSeat(Booking booking, ShowtimeSeatResponse seat, Instant now) {
        BookingSeat bookingSeat = new BookingSeat();
        bookingSeat.setBooking(booking);
        bookingSeat.setShowtimeSeatId(seat.getSeatId());
        bookingSeat.setPrice(seat.getPrice());
        bookingSeat.setSeatLabel(seat.getSeatLabel());
        bookingSeat.setCreatedAt(now);
        bookingSeat.setLastUpdatedAt(now);

        return bookingSeat;
    }
}
