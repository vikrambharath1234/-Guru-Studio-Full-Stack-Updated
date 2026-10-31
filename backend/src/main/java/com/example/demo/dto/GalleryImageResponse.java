package com.example.demo.dto;

import com.example.demo.entity.GalleryImage;

/** One gallery photo: where to load it from and which category it belongs to. */
public record GalleryImageResponse(Long id, String imageUrl, String category) {

	public static GalleryImageResponse from(GalleryImage image) {
		return new GalleryImageResponse(image.getId(), image.getImageUrl(), image.getCategory());
	}
}
