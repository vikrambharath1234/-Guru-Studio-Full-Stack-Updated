package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * One photo uploaded by the admin. The image file itself lives on Cloudinary;
 * this table keeps its web address (imageUrl), its Cloudinary id (publicId) and its category.
 */
@Entity
@Table(name = "gallery_images")
public class GalleryImage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "image_url", nullable = false, length = 500)
	private String imageUrl;

	// The id Cloudinary gives the file. We need it to delete the file later.
	@Column(name = "public_id", nullable = false, length = 255)
	private String publicId;

	@Column(nullable = false, length = 50)
	private String category;

	@Column(name = "created_at", nullable = false, updatable = false)
	private Instant createdAt;

	/** Required by JPA. Application code uses the other constructor. */
	protected GalleryImage() {
	}

	public GalleryImage(String imageUrl, String publicId, String category) {
		this.imageUrl = imageUrl;
		this.publicId = publicId;
		this.category = category;
		this.createdAt = Instant.now();
	}

	public Long getId() {
		return id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public String getPublicId() {
		return publicId;
	}

	public String getCategory() {
		return category;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}
}
