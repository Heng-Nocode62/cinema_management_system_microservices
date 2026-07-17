package com.heng.cms.cinemaservice.controller;

import com.heng.cms.cinemaservice.dto.SeatResponse;
import com.heng.cms.cinemaservice.service.SeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/seats")
@RequiredArgsConstructor
public class SeatController {
	private final SeatService seatService;
	
	@GetMapping("screen/{id}")
	public ResponseEntity<List<SeatResponse>> getSeatsByScreenId(
			@PathVariable("id") UUID id){
		return ResponseEntity.ok(seatService.getAllByScreenId(id));
	}
	@GetMapping("{id}")
	public ResponseEntity<SeatResponse> getSeatById(
			@PathVariable("id") UUID id

	){
		return ResponseEntity.ok(seatService.getById(id));
	}

}
