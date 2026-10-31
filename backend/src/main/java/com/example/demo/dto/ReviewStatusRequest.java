package com.example.demo.dto;

import com.example.demo.entity.ReviewStatus;
import jakarta.validation.constraints.NotNull;

/** The body the admin sends to approve or reject a review: {"status": "APPROVED"} */
public record ReviewStatusRequest(
		@NotNull(message = "Status is required.")
		ReviewStatus status) {
}
