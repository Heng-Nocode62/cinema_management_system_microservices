package com.heng.cms.bookingservice.repository;

import com.heng.cms.bookingservice.domain.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingSeatRepository  extends JpaRepository<BookingSeat, UUID>{

}
