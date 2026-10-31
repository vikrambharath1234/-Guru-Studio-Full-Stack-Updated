package com.example.demo.service;

import com.example.demo.dto.ContactMessageResponse;
import com.example.demo.dto.ContactRequest;
import com.example.demo.entity.ContactMessage;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.ContactMessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/** Saves contact form messages, and lets the admin read and delete them. */
@Service
public class ContactService {

	private final ContactMessageRepository contactMessageRepository;

	public ContactService(ContactMessageRepository contactMessageRepository) {
		this.contactMessageRepository = contactMessageRepository;
	}

	/** Called by the public contact form. Trims the text before saving. */
	@Transactional
	public void submit(ContactRequest request) {
		contactMessageRepository.save(new ContactMessage(
				request.name().trim(),
				request.phone().trim(),
				request.email().trim().toLowerCase(Locale.ROOT),
				request.subject().trim(),
				request.message().trim()));
	}

	/** Called by the admin dashboard. Newest messages first. */
	@Transactional(readOnly = true)
	public List<ContactMessageResponse> getAllMessages() {
		return contactMessageRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(ContactMessageResponse::from)
				.toList();
	}

	/** Called by the admin dashboard to delete one message. */
	@Transactional
	public void deleteMessage(Long id) {
		if (!contactMessageRepository.existsById(id)) {
			throw new ApiException(HttpStatus.NOT_FOUND, "Message not found.");
		}
		contactMessageRepository.deleteById(id);
	}
}
