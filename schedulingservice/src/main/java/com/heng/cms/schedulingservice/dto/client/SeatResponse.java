package com.heng.cms.schedulingservice.dto.client;

import com.heng.cms.schedulingservice.dto.client.enumeric.SeatType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeatResponse {
	private UUID id;
	private String label;
	private Integer seatNumber;
	private SeatType seatType;
	private Long groupCoupleId;

}
