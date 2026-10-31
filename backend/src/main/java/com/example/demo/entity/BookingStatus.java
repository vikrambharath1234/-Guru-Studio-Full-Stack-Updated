package com.example.demo.entity;

/** The state of a photography booking request. New bookings always start as PENDING. */
public enum BookingStatus {
	PENDING,
	CONFIRMED,
	REJECTED
}
