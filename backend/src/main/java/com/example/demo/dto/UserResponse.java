package com.example.demo.dto;

import com.example.demo.entity.User;

import java.time.Instant;

/**
 * The public view of a user. It never contains the password or its hash.
 */
public record UserResponse(
		Long id,
		String fullName,
		String email,
		String phone,
		String role,
		Instant createdAt) {

	public static UserResponse from(User user) {
		return new UserResponse(
				user.getId(),
				user.getFullName(),
				user.getEmail(),
				user.getPhone(),
				user.getRole().name(),
				user.getCreatedAt());
	}
}
