package com.example.demo.dto;

/** The numbers shown at the top of the admin dashboard. */
public record DashboardSummaryResponse(
		long totalBookings,
		long pendingBookings,
		long totalContactMessages,
		long pendingReviews,
		long totalGalleryImages) {
}
