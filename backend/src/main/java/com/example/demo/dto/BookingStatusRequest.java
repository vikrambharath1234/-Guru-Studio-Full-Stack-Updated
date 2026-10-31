package com.example.demo.dto;

import com.example.demo.entity.BookingStatus;
import jakarta.validation.constraints.NotNull;

/** The body the admin sends to change a booking's status: {"status": "CONFIRMED"} */
public record BookingStatusRequest(
		@NotNull(message = "Status is required.")
		BookingStatus status) {
}
