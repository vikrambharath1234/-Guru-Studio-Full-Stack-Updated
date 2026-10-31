package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * A message sent through the website contact form. Saved in the "contact_messages" table.
 * The admin reads and deletes these through /api/admin/contact-messages.
 */
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	// Nullable in the database so rows saved before the phone column existed still load.
	@Column(length = 30)
	private String phone;

	@Column(nullable = false, length = 150)
	private String email;

	@Column(nullable = false, length = 150)
	private String subject;

	@Column(nullable = false, length = 2000)
	private String message;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	/** Required by JPA. Application code uses the other constructor. */
	protected ContactMessage() {
	}

	public ContactMessage(String name, String phone, String email, String subject, String message) {
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.subject = subject;
		this.message = message;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public String getSubject() {
		return subject;
	}

	public String getMessage() {
		return message;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
