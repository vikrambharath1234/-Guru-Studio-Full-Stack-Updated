package com.example.demo.controller;

import com.example.demo.dto.ContactMessageResponse;
import com.example.demo.service.ContactService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Admin only (see SecurityConfig). Lists and deletes contact messages. */
@RestController
@RequestMapping("/api/admin/contact-messages")
public class AdminContactController {

	private final ContactService contactService;

	public AdminContactController(ContactService contactService) {
		this.contactService = contactService;
	}

	@GetMapping
	public List<ContactMessageResponse> listMessages() {
		return contactService.getAllMessages();
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
		contactService.deleteMessage(id);
		return ResponseEntity.noContent().build();
	}
}
