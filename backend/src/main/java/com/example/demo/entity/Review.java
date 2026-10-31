package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/** A customer review. Saved in the "reviews" table. */
@Entity
@Table(name = "reviews")
public class Review {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_name", nullable = false, length = 100)
	private String customerName;

	@Column(nullable = false)
	private int rating;

	@Column(nullable = false, length = 1000)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private ReviewStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	/** Required by JPA. Application code uses the other constructor. */
	protected Review() {
	}

	public Review(String customerName, int rating, String message) {
		this.customerName = customerName;
		this.rating = rating;
		this.message = message;
		this.status = ReviewStatus.PENDING; // the admin must approve it first
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public int getRating() {
		return rating;
	}

	public String getMessage() {
		return message;
	}

	public ReviewStatus getStatus() {
		return status;
	}

	public void setStatus(ReviewStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
