package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** The data sent from the website contact form. */
public record ContactRequest(
		@NotBlank(message = "Name is required.")
		@Size(max = 100, message = "Name must be 100 characters or fewer.")
		String name,

		@NotBlank(message = "Phone number is required.")
		@Pattern(regexp = "^[0-9+\\- ]{7,20}$", message = "Enter a valid phone number.")
		String phone,

		@NotBlank(message = "Email is required.")
		@Email(message = "Enter a valid email address.")
		@Size(max = 150, message = "Email must be 150 characters or fewer.")
		String email,

		@NotBlank(message = "Subject is required.")
		@Size(max = 150, message = "Subject must be 150 characters or fewer.")
		String subject,

		@NotBlank(message = "Message is required.")
		@Size(max = 2000, message = "Message must be 2000 characters or fewer.")
		String message) {
}
