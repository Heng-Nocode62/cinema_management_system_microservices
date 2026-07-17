package com.heng.cms.bookingservice.repository;

import com.heng.cms.bookingservice.domain.Booking;
import com.heng.cms.bookingservice.domain.enumeric.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface BookingRepository  extends JpaRepository<Booking, UUID>, JpaSpecificationExecutor<Booking> {

	
	@Query("""
			SELECT b
			FROM Booking b
			WHERE b.status = :status
			AND b.expiresAt <= :now
			""")
	List<Booking> findExpiredBookings(Instant now, BookingStatus status);
//
//	@Query("""
//			SELECT b
//			FROM Booking b
//			WHERE b.createdBy = :id
//			""")
//
//	List<Booking> findAllByUserId(UUID id);
	

}
