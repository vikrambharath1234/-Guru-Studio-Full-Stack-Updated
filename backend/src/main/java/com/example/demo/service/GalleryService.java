package com.example.demo.service;

import com.example.demo.dto.GalleryImageResponse;
import com.example.demo.entity.GalleryImage;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.GalleryImageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/** Gallery photos: uploading (admin), listing for the website, and deleting (admin). */
@Service
public class GalleryService {

	/** These must match the category ids used on the website's gallery filter buttons. */
	public static final List<String> CATEGORIES =
			List.of("weddings", "engagements", "puberty", "birthdays", "valaikappu");

	private static final List<String> ALLOWED_TYPES = List.of("image/jpeg", "image/png", "image/webp");

	private final GalleryImageRepository galleryImageRepository;
	private final CloudinaryService cloudinaryService;

	public GalleryService(GalleryImageRepository galleryImageRepository, CloudinaryService cloudinaryService) {
		this.galleryImageRepository = galleryImageRepository;
		this.cloudinaryService = cloudinaryService;
	}

	/** Public website: all photos, or only one category if a category is given. */
	@Transactional(readOnly = true)
	public List<GalleryImageResponse> getPublicImages(String category) {
		List<GalleryImage> images = (category == null || category.isBlank())
				? galleryImageRepository.findAllByOrderByCreatedAtDesc()
				: galleryImageRepository.findByCategoryOrderByCreatedAtDesc(category);
		return images.stream().map(GalleryImageResponse::from).toList();
	}

	/** Admin: every uploaded photo (same list as the website, shown with delete buttons). */
	@Transactional(readOnly = true)
	public List<GalleryImageResponse> getAllImages() {
		return getPublicImages(null);
	}

	/** Admin: uploads one or more photos into one category. Each photo goes to Cloudinary first. */
	@Transactional
	public List<GalleryImageResponse> uploadImages(List<MultipartFile> files, String category) {
		if (!CATEGORIES.contains(category)) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Choose a valid category.");
		}
		if (files == null || files.isEmpty()) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Choose at least one photo to upload.");
		}

		// Check every file before uploading any, so a bad file does not leave half an upload behind
		for (MultipartFile file : files) {
			if (file.isEmpty() || !ALLOWED_TYPES.contains(file.getContentType())) {
				throw new ApiException(HttpStatus.BAD_REQUEST, "Only JPG, PNG or WEBP photos can be uploaded.");
			}
		}

		List<GalleryImageResponse> saved = new ArrayList<>();
		for (MultipartFile file : files) {
			CloudinaryService.CloudinaryImage uploaded = cloudinaryService.upload(file, category);
			GalleryImage image = galleryImageRepository.save(
					new GalleryImage(uploaded.url(), uploaded.publicId(), category));
			saved.add(GalleryImageResponse.from(image));
		}
		return saved;
	}

	/** Admin: deletes the file from Cloudinary, then removes the row from MySQL. */
	@Transactional
	public void deleteImage(Long id) {
		GalleryImage image = galleryImageRepository.findById(id)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Photo not found."));
		cloudinaryService.delete(image.getPublicId());
		galleryImageRepository.delete(image);
	}
}
