package com.example.demo.controller;

import com.example.demo.dto.GalleryImageResponse;
import com.example.demo.service.GalleryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/** Admin only (see SecurityConfig). Uploads, lists and deletes gallery photos. */
@RestController
@RequestMapping("/api/admin/gallery")
public class AdminGalleryController {

	private final GalleryService galleryService;

	public AdminGalleryController(GalleryService galleryService) {
		this.galleryService = galleryService;
	}

	@GetMapping
	public List<GalleryImageResponse> listImages() {
		return galleryService.getAllImages();
	}

	/**
	 * POST /api/admin/gallery  (multipart form: "files" = one or more photos, "category" = e.g. weddings)
	 */
	@PostMapping
	public ResponseEntity<List<GalleryImageResponse>> upload(
			@RequestParam("files") List<MultipartFile> files,
			@RequestParam("category") String category) {
		return ResponseEntity.status(HttpStatus.CREATED).body(galleryService.uploadImages(files, category));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
		galleryService.deleteImage(id);
		return ResponseEntity.noContent().build();
	}
}
