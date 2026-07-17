package com.heng.cms.schedulingservice.repository;

import com.heng.cms.schedulingservice.domain.Showtime;
import com.heng.cms.schedulingservice.domain.enumeric.ShowtimeStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShowtimeRepository extends JpaRepository<Showtime, UUID> {
	
	@Query("""
		    SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
		    FROM Showtime s
		    WHERE s.screenId = :screenId
				AND s.status = 'SCHEDULED'
		      AND s.startTime < :endTime
		      AND s.endTime > :startTime
		""")
		boolean existsOverlappingShowtime(UUID screenId,
		                                  Instant startTime,
		                                  Instant endTime);
	
	
	@Query("""
			SELECT s
			FROM Showtime s
			WHERE s.endTime <= :now
			AND s.status = :status
			""")
	List<Showtime> findFinishedShowtimes(Instant now, ShowtimeStatus status);

	Optional<Showtime> findByIdAndStatus(UUID id, ShowtimeStatus status);

	@Query("""
		SELECT s
		FROM Showtime s
		WHERE s.status = 'SCHEDULED'
		""")
	Page<Showtime> findAllScheduledShowtimes(Pageable pageable);
}
