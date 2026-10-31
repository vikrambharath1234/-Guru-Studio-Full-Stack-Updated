package com.example.demo.controller;

import com.example.demo.dto.ApiMessageResponse;
import com.example.demo.dto.BookingRequest;
import com.example.demo.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public booking form. Customers can send a request but cannot see other bookings. */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	private final BookingService bookingService;

	public BookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	/** POST /api/bookings -> 201. The booking is PENDING until the studio confirms it. */
	@PostMapping
	public ResponseEntity<ApiMessageResponse> submit(@Valid @RequestBody BookingRequest request) {
		bookingService.createBooking(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiMessageResponse("Thank you. Your booking request has been received. We will confirm it soon."));
	}
}
