package com.example.demo.service;

import com.example.demo.dto.ReviewRequest;
import com.example.demo.dto.ReviewResponse;
import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.ReviewRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Customer reviews: saving them, showing approved ones, and the admin approve/reject step. */
@Service
public class ReviewService {

	private final ReviewRepository reviewRepository;

	public ReviewService(ReviewRepository reviewRepository) {
		this.reviewRepository = reviewRepository;
	}

	/** Called by the public review form. The review waits as PENDING until the admin approves it. */
	@Transactional
	public void submitReview(ReviewRequest request) {
		reviewRepository.save(new Review(
				request.customerName().trim(),
				request.rating(),
				request.message().trim()));
	}

	/** Public website: only APPROVED reviews. */
	@Transactional(readOnly = true)
	public List<ReviewResponse> getApprovedReviews() {
		return reviewRepository.findByStatusOrderByCreatedAtDesc(ReviewStatus.APPROVED).stream()
				.map(ReviewResponse::from)
				.toList();
	}

	/** Admin dashboard: every review, newest first. */
	@Transactional(readOnly = true)
	public List<ReviewResponse> getAllReviews() {
		return reviewRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(ReviewResponse::from)
				.toList();
	}

	/** Admin approves or rejects a review. PENDING is not allowed here, because it is the starting state. */
	@Transactional
	public ReviewResponse updateStatus(Long id, ReviewStatus newStatus) {
		if (newStatus == ReviewStatus.PENDING) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Status must be APPROVED or REJECTED.");
		}
		Review review = reviewRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Review not found."));
		review.setStatus(newStatus);
		return ReviewResponse.from(review);
	}
}
