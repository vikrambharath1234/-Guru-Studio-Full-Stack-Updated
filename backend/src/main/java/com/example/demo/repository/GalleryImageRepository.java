package com.example.demo.repository;

import com.example.demo.entity.GalleryImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GalleryImageRepository extends JpaRepository<GalleryImage, Long> {

	/** Newest photos first, for every category. */
	List<GalleryImage> findAllByOrderByCreatedAtDesc();

	/** Newest photos first, for one category (for example "weddings"). */
	List<GalleryImage> findByCategoryOrderByCreatedAtDesc(String category);
}
