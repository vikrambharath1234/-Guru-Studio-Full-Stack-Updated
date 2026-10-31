package com.example.demo.repository;

import com.example.demo.entity.Review;
import com.example.demo.entity.ReviewStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

	List<Review> findAllByOrderByCreatedAtDesc();

	List<Review> findByStatusOrderByCreatedAtDesc(ReviewStatus status);

	long countByStatus(ReviewStatus status);
}
