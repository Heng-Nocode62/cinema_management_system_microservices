package com.heng.cms.schedulingservice.repository;

import com.heng.cms.schedulingservice.domain.ShowtimeSeat;
import com.heng.cms.schedulingservice.domain.enumeric.SeatStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ShowtimeSeatRepository extends JpaRepository<ShowtimeSeat, UUID> {

	int countByShowtimeId(UUID showtimeId);

	int countByShowtimeIdAndStatus(UUID showtimeId, SeatStatus status);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("""
			SELECT s
			FROM ShowtimeSeat s
			WHERE s.id IN :ids
			""")
	List<ShowtimeSeat> findAllByIdWithLock(List<UUID> ids);

//	@Query("""
//			SELECT bs.showtimeSeat
//			FROM BookingSeat bs
//			WHERE bs.booking.id = :id
//			""")
//
//	List<ShowtimeSeat> findAllByBookingId(UUID id);

	List<ShowtimeSeat> findByShowtimeId(UUID showtimeId);

	boolean existsByShowtimeIdAndStatus(UUID showtimeId, SeatStatus status);

}
