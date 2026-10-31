package com.example.demo.controller;

import com.example.demo.dto.GalleryImageResponse;
import com.example.demo.service.GalleryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Public gallery: GET /api/gallery or GET /api/gallery?category=weddings */
@RestController
@RequestMapping("/api/gallery")
public class GalleryController {

	private final GalleryService galleryService;

	public GalleryController(GalleryService galleryService) {
		this.galleryService = galleryService;
	}

	@GetMapping
	public List<GalleryImageResponse> getImages(@RequestParam(name = "category", required = false) String category) {
		return galleryService.getPublicImages(category);
	}
}
