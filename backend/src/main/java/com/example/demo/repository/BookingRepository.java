package com.example.demo.repository;

import com.example.demo.entity.Booking;
import com.example.demo.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

	/** Newest bookings first. Spring builds this query from the method name. */
	List<Booking> findAllByOrderByCreatedAtDesc();

	long countByStatus(BookingStatus status);
}
