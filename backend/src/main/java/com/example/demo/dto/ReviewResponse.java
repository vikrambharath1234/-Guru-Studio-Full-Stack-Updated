package com.example.demo.dto;

import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewStatus;

import java.time.Instant;

/** A review. The public website only receives APPROVED ones. */
public record ReviewResponse(
		Long id,
		String customerName,
		int rating,
		String message,
		ReviewStatus status,
		Instant createdAt) {

	public static ReviewResponse from(Review review) {
		return new ReviewResponse(
				review.getId(),
				review.getCustomerName(),
				review.getRating(),
				review.getMessage(),
				review.getStatus(),
				review.getCreatedAt());
	}
}
