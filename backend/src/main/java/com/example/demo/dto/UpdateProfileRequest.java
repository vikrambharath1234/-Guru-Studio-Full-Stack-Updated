package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * The fields a user may change on their own profile.
 * Email and role cannot be changed here. A user cannot promote themselves to ADMIN.
 */
public record UpdateProfileRequest(
		@NotBlank(message = "Full name is required.")
		@Size(max = 100, message = "Full name must be 100 characters or fewer.")
		String fullName,

		@Size(max = 30, message = "Phone number must be 30 characters or fewer.")
		String phone) {
}
