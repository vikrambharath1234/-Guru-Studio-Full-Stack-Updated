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
import java.time.LocalDate;

/** A photography booking request from the website. Saved in the "bookings" table. */
@Entity
@Table(name = "bookings")
public class Booking {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_name", nullable = false, length = 100)
	private String customerName;

	@Column(nullable = false, length = 30)
	private String phone;

	@Column(nullable = false, length = 150)
	private String email;

	@Column(name = "event_type", nullable = false, length = 50)
	private String eventType;

	@Column(name = "event_date", nullable = false)
	private LocalDate eventDate;

	@Column(nullable = false, length = 200)
	private String location;

	@Column(length = 2000)
	private String details;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private BookingStatus status;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	/** Required by JPA. Application code uses the other constructor. */
	protected Booking() {
	}

	public Booking(String customerName, String phone, String email, String eventType,
			LocalDate eventDate, String location, String details) {
		this.customerName = customerName;
		this.phone = phone;
		this.email = email;
		this.eventType = eventType;
		this.eventDate = eventDate;
		this.location = location;
		this.details = details;
		this.status = BookingStatus.PENDING; // never confirmed automatically
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getEventType() {
		return eventType;
	}

	public LocalDate getEventDate() {
		return eventDate;
	}

	public String getLocation() {
		return location;
	}

	public String getDetails() {
		return details;
	}

	public BookingStatus getStatus() {
		return status;
	}

	public void setStatus(BookingStatus status) {
		this.status = status;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
