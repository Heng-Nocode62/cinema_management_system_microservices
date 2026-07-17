package com.heng.cms.cinemaservice.controller;

import com.heng.cms.cinemaservice.dto.PageResponse;
import com.heng.cms.cinemaservice.dto.ScreenRequest;
import com.heng.cms.cinemaservice.dto.ScreenResponse;
import com.heng.cms.cinemaservice.service.ScreenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/screens")
@RequiredArgsConstructor
public class ScreenController {
	private final ScreenService screenService;
	
	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ScreenResponse> createScreen(
			@Valid @RequestBody ScreenRequest request){
		return new ResponseEntity<>(screenService.create(request), HttpStatus.CREATED);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<ScreenResponse> getScreenById(@PathVariable("id") UUID id){
		return ResponseEntity.ok(screenService.getById(id));
	}
	@GetMapping
	public ResponseEntity<PageResponse<ScreenResponse>> getAllScreen(
			@RequestParam(name = "page", defaultValue = "0", required = false) int page,
			@RequestParam(name = "size", defaultValue = "10", required = false) int size){
		return ResponseEntity.ok(screenService.getAll(page,size));
		
	}
	
	
	// TODO updateScreen

}
