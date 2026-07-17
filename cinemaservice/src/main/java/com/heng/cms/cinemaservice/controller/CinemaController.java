package com.heng.cms.cinemaservice.controller;

import com.heng.cms.cinemaservice.dto.CinemaRequest;
import com.heng.cms.cinemaservice.dto.CinemaResponse;
import com.heng.cms.cinemaservice.dto.CinemaUpdateRequest;
import com.heng.cms.cinemaservice.dto.PageResponse;
import com.heng.cms.cinemaservice.service.CinemaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("api/v1/cinemas")
@RequiredArgsConstructor
public class CinemaController {
	private final CinemaService cinemaService;

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<CinemaResponse> createCinema(@Valid @RequestBody CinemaRequest request){
		return ResponseEntity.ok().body(cinemaService.create(request));
	}
	@GetMapping
	public ResponseEntity<PageResponse<CinemaResponse>> getAllCinema(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name ="size", defaultValue ="10", required = false) int size){
		return ResponseEntity.ok().body(cinemaService.getAll(page, size));
		
	}
	@GetMapping("{id}")
	public ResponseEntity<CinemaResponse> getCinemaById(@PathVariable("id") UUID id){
		return ResponseEntity.ok().body(cinemaService.getById(id));
	}

	@PutMapping("{id}")
	@PreAuthorize("hasRole('ADMIN')")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateCinema(@PathVariable("id") UUID id, @Valid @RequestBody CinemaUpdateRequest request){
		cinemaService.update(id, request);
	}
	@PostMapping("/batch")
	public ResponseEntity<List<CinemaResponse>> getCinemas(@RequestBody @NotNull @NotEmpty List<UUID> cinemaIds){
		return ResponseEntity.ok(cinemaService.getAllByIds(cinemaIds));
	}
}
