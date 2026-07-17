package com.heng.cms.bookingservice.spec;

import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class BookingFilter {
    private UUID userId;
    private UUID showtimeId;
    private BookingStatus status;
    private Instant createdAfter;
}
