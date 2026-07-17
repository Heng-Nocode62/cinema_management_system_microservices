package com.heng.cms.schedulingservice.controller;

import com.heng.cms.schedulingservice.dto.*;
import com.heng.cms.schedulingservice.service.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {
	private final ShowtimeService showtimeService;
	
	@PostMapping
	public ResponseEntity<ShowtimeResponse> createShowtime(@Valid @RequestBody CreateShowtimeRequest request){
		return ResponseEntity.ok().body(showtimeService.create(request));
	}
	
	@GetMapping
	public ResponseEntity<PageResponse<ShowtimeResponse>> getAllShowtime(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue ="10", required = false) int size
			){
		return ResponseEntity.ok(showtimeService.getAllShowtime(page, size));
	}
	
	@GetMapping("{id}")
	public ResponseEntity<ShowtimeResponse> getShowtimeById(@PathVariable("id") UUID id){
		return ResponseEntity.ok(
				showtimeService.getShowtimeById(id)
				);
	}
	

	@DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        showtimeService.cancelShowtime(id);
        return ResponseEntity.noContent().build();
    }
	
	@PutMapping("/{id}")

	public ResponseEntity<ShowtimeResponse> update(
	        @PathVariable UUID id,
	        @RequestBody UpdateShowtimeRequest request
	) {
	    return ResponseEntity.ok(showtimeService.updateShowtime(id, request));
	}

	@GetMapping("{id}/seats")
	public ResponseEntity<List<ShowtimeSeatResponse>> getShowtimeSeatsByShowtimeId(
			@PathVariable UUID id){
		return ResponseEntity.ok(showtimeService.getShowtimeSeatsByShowtimeId(id));
	}
}
