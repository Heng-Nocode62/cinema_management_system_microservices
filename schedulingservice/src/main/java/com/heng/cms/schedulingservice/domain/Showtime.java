package com.heng.cms.schedulingservice.domain;

import com.heng.cms.schedulingservice.domain.enumeric.ShowtimeStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "show_times")
public class Showtime {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private Instant startTime;

	@Column(nullable = false)
	private Instant endTime;

	@Column(nullable = false)
	private BigDecimal basePrice;

	@Enumerated(EnumType.STRING)
	private ShowtimeStatus status;

	private UUID movieId;

	private UUID screenId;

	private Instant createdAt;
	private Instant lastUpdatedAt;
}
