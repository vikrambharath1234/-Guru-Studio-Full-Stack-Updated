package com.example.demo.service;

import com.example.demo.dto.BookingRequest;
import com.example.demo.dto.BookingResponse;
import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.BookingRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/** Saves booking requests, and lets the admin list them and change their status. */
@Service
public class BookingService {

	private final BookingRepository bookingRepository;

	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}

	/** Called by the public booking form. The new booking is always PENDING. */
	@Transactional
	public void createBooking(BookingRequest request) {
		bookingRepository.save(new Booking(
				request.customerName().trim(),
				request.phone().trim(),
				request.email().trim().toLowerCase(Locale.ROOT),
				request.eventType().trim(),
				request.eventDate(),
				request.location().trim(),
				blankToNull(request.details())));
	}

	/** Called by the admin dashboard. Newest bookings first. */
	@Transactional(readOnly = true)
	public List<BookingResponse> getAllBookings() {
		return bookingRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(BookingResponse::from)
				.toList();
	}

	/**
	 * Called by the admin dashboard to set a booking to PENDING, CONFIRMED or REJECTED.
	 * Saving happens automatically when the method finishes, because of @Transactional.
	 */
	@Transactional
	public BookingResponse updateStatus(Long id, BookingStatus newStatus) {
		Booking booking = bookingRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Booking not found."));
		booking.setStatus(newStatus);
		return BookingResponse.from(booking);
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
