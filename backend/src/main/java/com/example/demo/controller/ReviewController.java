package com.example.demo.controller;

import com.example.demo.dto.ApiMessageResponse;
import com.example.demo.dto.ReviewRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public review endpoints: send a review, and read the approved ones. */
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	/** POST /api/reviews -> 201. The review stays hidden until the admin approves it. */
	@PostMapping
	public ResponseEntity<ApiMessageResponse> submit(@Valid @RequestBody ReviewRequest request) {
		reviewService.submitReview(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiMessageResponse("Thank you for your review. It will appear after it is approved."));
	}

	/** GET /api/reviews -> only the APPROVED reviews. */
	@GetMapping
	public List<ReviewResponse> getApproved() {
		return reviewService.getApprovedReviews();
	}
}
