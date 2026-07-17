package com.heng.cms.bookingservice.repository;

import com.heng.cms.bookingservice.domain.BookingConcession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingConcessionRepository extends JpaRepository<BookingConcession, UUID> {
}
