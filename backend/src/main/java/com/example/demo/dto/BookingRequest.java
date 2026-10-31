package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/** The data sent when a customer asks to book a photography session. */
public record BookingRequest(
		@NotBlank(message = "Name is required.")
		@Size(max = 100, message = "Name must be 100 characters or fewer.")
		String customerName,

		@NotBlank(message = "Phone number is required.")
		@Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Enter a valid phone number.")
		String phone,

		@NotBlank(message = "Email is required.")
		@Email(message = "Enter a valid email address.")
		@Size(max = 150, message = "Email must be 150 characters or fewer.")
		String email,

		@NotBlank(message = "Event type is required.")
		@Size(max = 50, message = "Event type must be 50 characters or fewer.")
		String eventType,

		@NotNull(message = "Event date is required.")
		LocalDate eventDate,

		@NotBlank(message = "Event location is required.")
		@Size(max = 200, message = "Location must be 200 characters or fewer.")
		String location,

		@Size(max = 2000, message = "Details must be 2000 characters or fewer.")
		String details) {
}
