package com.example.demo.service;

import com.example.demo.dto.ContactRequest;
import com.example.demo.entity.ContactMessage;
import com.example.demo.repository.ContactMessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

	@Mock
	private ContactMessageRepository contactMessageRepository;

	private ContactService contactService;

	@BeforeEach
	void setUp() {
		contactService = new ContactService(contactMessageRepository);
	}

	@Test
	void submitSavesTrimmedMessageFields() {
		contactService.submit(new ContactRequest(
				"  Jane Doe ", " 9842642268 ", " Jane@Example.com ", " Wedding enquiry ", "  Hello, I would like a quote.  "));

		ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
		verify(contactMessageRepository).save(captor.capture());
		ContactMessage saved = captor.getValue();

		assertEquals("Jane Doe", saved.getName());
		assertEquals("9842642268", saved.getPhone());
		assertEquals("jane@example.com", saved.getEmail());
		assertEquals("Wedding enquiry", saved.getSubject());
		assertEquals("Hello, I would like a quote.", saved.getMessage());
		assertNotNull(saved.getCreatedAt());
	}

	@Test
	void submitCallsRepositoryOnce() {
		contactService.submit(new ContactRequest("Jane", "9842642268", "jane@example.com", "Subject", "Message"));

		verify(contactMessageRepository).save(any(ContactMessage.class));
	}
}
