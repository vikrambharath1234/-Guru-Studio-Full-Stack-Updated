package com.example.demo.controller;

import com.example.demo.dto.BookingResponse;
import com.example.demo.dto.BookingStatusRequest;
import com.example.demo.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin only (see SecurityConfig). Lists bookings and changes their status. */
@RestController
@RequestMapping("/api/admin/bookings")
public class AdminBookingController {

	private final BookingService bookingService;

	public AdminBookingController(BookingService bookingService) {
		this.bookingService = bookingService;
	}

	@GetMapping
	public List<BookingResponse> listBookings() {
		return bookingService.getAllBookings();
	}

	/** PUT /api/admin/bookings/{id}/status with body {"status": "CONFIRMED"} */
	@PutMapping("/{id}/status")
	public BookingResponse updateStatus(@PathVariable Long id, @Valid @RequestBody BookingStatusRequest request) {
		return bookingService.updateStatus(id, request.status());
	}
}
