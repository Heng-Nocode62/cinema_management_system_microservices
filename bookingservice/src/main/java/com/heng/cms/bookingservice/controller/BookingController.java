package com.heng.cms.bookingservice.controller;
import com.heng.cms.bookingservice.dto.BookingResponse;
import com.heng.cms.bookingservice.dto.CreateBookingRequest;
import com.heng.cms.bookingservice.dto.PageResponse;
import com.heng.cms.bookingservice.service.BookingService;
import com.heng.cms.bookingservice.spec.BookingFilter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/bookings")

@RequiredArgsConstructor
public class BookingController {
	private final BookingService bookingService;
	
	@PostMapping
	public ResponseEntity<BookingResponse> createBooking(
			@Valid @RequestBody CreateBookingRequest request){
		
		return ResponseEntity.ok(
				bookingService.createBooking(request)
				);
	}
	@PutMapping("/confirm/{bookingId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void confirmBooking(
			@PathVariable() UUID bookingId){

		bookingService.confirmBooking(bookingId);
	}

//	@GetMapping("{id}")
//	public ResponseEntity<BookingResponse> getById(@PathVariable UUID id){
//		return ResponseEntity.ok(bookingService.getById(id));
//	}

//	@GetMapping
//	public ResponseEntity<PageResponse<BookingResponse>> getAllBookings(@RequestParam(name = "page", defaultValue = "0", required = false) int page,
//																		@RequestParam(name = "size", defaultValue = "10", required = false) int size,
//																		@RequestBody(required = false) BookingFilter filter){
//		return ResponseEntity.ok(bookingService.getAllBooking(page, size, filter));
//	}
//
//	@PutMapping("{id}/cancel")
//	@PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
//	@ResponseStatus(HttpStatus.NO_CONTENT)
//	public void cancelBookingById(@PathVariable UUID id){
//		bookingService.cancelBookingById(id);
//	}

}