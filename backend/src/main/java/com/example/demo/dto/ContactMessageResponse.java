package com.example.demo.dto;

import com.example.demo.entity.ContactMessage;

import java.time.Instant;

/** A contact message as the admin dashboard sees it. */
public record ContactMessageResponse(
		Long id,
		String name,
		String phone,
		String email,
		String subject,
		String message,
		Instant createdAt) {

	public static ContactMessageResponse from(ContactMessage contactMessage) {
		return new ContactMessageResponse(
				contactMessage.getId(),
				contactMessage.getName(),
				contactMessage.getPhone(),
				contactMessage.getEmail(),
				contactMessage.getSubject(),
				contactMessage.getMessage(),
				contactMessage.getCreatedAt());
	}
}
