package com.example.demo.service;

import com.example.demo.dto.DashboardSummaryResponse;
import com.example.demo.entity.BookingStatus;
import com.example.demo.entity.ReviewStatus;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ContactMessageRepository;
import com.example.demo.repository.GalleryImageRepository;
import com.example.demo.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Counts for the numbers at the top of the admin dashboard. */
@Service
public class DashboardService {

	private final BookingRepository bookingRepository;
	private final ContactMessageRepository contactMessageRepository;
	private final ReviewRepository reviewRepository;
	private final GalleryImageRepository galleryImageRepository;

	public DashboardService(BookingRepository bookingRepository,
			ContactMessageRepository contactMessageRepository,
			ReviewRepository reviewRepository,
			GalleryImageRepository galleryImageRepository) {
		this.bookingRepository = bookingRepository;
		this.contactMessageRepository = contactMessageRepository;
		this.reviewRepository = reviewRepository;
		this.galleryImageRepository = galleryImageRepository;
	}

	@Transactional(readOnly = true)
	public DashboardSummaryResponse getSummary() {
		return new DashboardSummaryResponse(
				bookingRepository.count(),
				bookingRepository.countByStatus(BookingStatus.PENDING),
				contactMessageRepository.count(),
				reviewRepository.countByStatus(ReviewStatus.PENDING),
				galleryImageRepository.count());
	}
}
