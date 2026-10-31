package com.example.demo.controller;

import com.example.demo.dto.ApiMessageResponse;
import com.example.demo.dto.ContactRequest;
import com.example.demo.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public contact form endpoint. Customers can send a message but cannot read messages.
 * The admin reads them through AdminContactController.
 */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

	private final ContactService contactService;

	public ContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	/** POST /api/contact -> 201 with a thank-you message. */
	@PostMapping
	public ResponseEntity<ApiMessageResponse> submit(@Valid @RequestBody ContactRequest request) {
		contactService.submit(request);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new ApiMessageResponse("Thank you. Your message has been received."));
	}
}
