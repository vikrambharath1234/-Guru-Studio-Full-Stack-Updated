package com.example.demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** The data sent when a customer writes a review. */
public record ReviewRequest(
		@NotBlank(message = "Name is required.")
		@Size(max = 100, message = "Name must be 100 characters or fewer.")
		String customerName,

		@NotNull(message = "Rating is required.")
		@Min(value = 1, message = "Rating must be between 1 and 5.")
		@Max(value = 5, message = "Rating must be between 1 and 5.")
		Integer rating,

		@NotBlank(message = "Review message is required.")
		@Size(max = 1000, message = "Review must be 1000 characters or fewer.")
		String message) {
}
