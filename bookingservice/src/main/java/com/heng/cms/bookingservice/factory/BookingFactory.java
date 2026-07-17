package com.heng.cms.bookingservice.factory;

import com.heng.cms.bookingservice.domain.Booking;
import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingFactory {
    
    public Booking createPendingBooking(UUID showtimeId, UUID userAccountId, Instant now) {
        Booking booking = new Booking();
        booking.setShowtimeId(showtimeId);
        booking.setStatus(BookingStatus.PENDING);
        booking.setUserAccountId(userAccountId);
        booking.setBookingSeats(new ArrayList<>());
        booking.setTotalPrice(BigDecimal.ZERO);
        booking.setCreatedAt(now);
        //TODO use configuration properties
        booking.setExpiresAt(now.plusSeconds(90));
        return booking;
    }
    
}
