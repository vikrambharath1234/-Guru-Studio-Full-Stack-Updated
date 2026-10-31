package com.example.demo.dto;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;

import java.time.Instant;
import java.time.LocalDate;

/** A booking request as the admin dashboard sees it. */
public record BookingResponse(
		Long id,
		String customerName,
		String phone,
		String email,
		String eventType,
		LocalDate eventDate,
		String location,
		String details,
		BookingStatus status,
		Instant createdAt) {

	public static BookingResponse from(Booking booking) {
		return new BookingResponse(
				booking.getId(),
				booking.getCustomerName(),
				booking.getPhone(),
				booking.getEmail(),
				booking.getEventType(),
				booking.getEventDate(),
				booking.getLocation(),
				booking.getDetails(),
				booking.getStatus(),
				booking.getCreatedAt());
	}
}
