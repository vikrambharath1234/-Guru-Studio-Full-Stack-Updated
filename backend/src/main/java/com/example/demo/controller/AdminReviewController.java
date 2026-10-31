package com.example.demo.controller;

import com.example.demo.dto.ReviewResponse;
import com.example.demo.dto.ReviewStatusRequest;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin only (see SecurityConfig). Lists reviews and approves or rejects them. */
@RestController
@RequestMapping("/api/admin/reviews")
public class AdminReviewController {

	private final ReviewService reviewService;

	public AdminReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@GetMapping
	public List<ReviewResponse> listReviews() {
		return reviewService.getAllReviews();
	}

	/** PUT /api/admin/reviews/{id}/status with body {"status": "APPROVED"} or {"status": "REJECTED"} */
	@PutMapping("/{id}/status")
	public ReviewResponse updateStatus(@PathVariable Long id, @Valid @RequestBody ReviewStatusRequest request) {
		return reviewService.updateStatus(id, request.status());
	}
}
