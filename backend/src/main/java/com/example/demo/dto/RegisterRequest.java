package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** The data sent when someone creates an account. */
public record RegisterRequest(
		@NotBlank(message = "Full name is required.")
		@Size(max = 100, message = "Full name must be 100 characters or fewer.")
		String fullName,

		@NotBlank(message = "Email is required.")
		@Email(message = "Enter a valid email address.")
		@Size(max = 150, message = "Email must be 150 characters or fewer.")
		String email,

		// BCrypt only uses the first 72 bytes, so longer passwords are refused.
		@NotBlank(message = "Password is required.")
		@Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
		String password) {
}
