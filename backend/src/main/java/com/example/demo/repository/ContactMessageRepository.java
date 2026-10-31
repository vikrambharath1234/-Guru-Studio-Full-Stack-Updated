package com.example.demo.repository;

import com.example.demo.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

	/** Newest messages first, for the admin list. */
	List<ContactMessage> findAllByOrderByCreatedAtDesc();
}
