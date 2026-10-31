package com.example.demo.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Checks the validation rules without starting Spring or a database. */
class ContactRequestValidationTest {

	private static ValidatorFactory factory;
	private static Validator validator;

	@BeforeAll
	static void createValidator() {
		factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@AfterAll
	static void closeValidator() {
		factory.close();
	}

	@Test
	void validContactRequestHasNoErrors() {
		ContactRequest request = new ContactRequest("Jane Doe", "9842642268", "jane@example.com", "Wedding", "I need a quote.");

		Set<ConstraintViolation<ContactRequest>> violations = validator.validate(request);

		assertTrue(violations.isEmpty());
	}

	@Test
	void blankNameIsRejected() {
		ContactRequest request = new ContactRequest("   ", "9842642268", "jane@example.com", "Wedding", "I need a quote.");

		Set<ConstraintViolation<ContactRequest>> violations = validator.validate(request);

		assertEquals(1, violations.size());
		assertEquals("name", violations.iterator().next().getPropertyPath().toString());
	}

	@Test
	void invalidEmailIsRejected() {
		ContactRequest request = new ContactRequest("Jane", "9842642268", "not-an-email", "Wedding", "I need a quote.");

		Set<ConstraintViolation<ContactRequest>> violations = validator.validate(request);

		assertEquals(1, violations.size());
		assertEquals("email", violations.iterator().next().getPropertyPath().toString());
	}

	@Test
	void missingSubjectAndMessageAreRejected() {
		ContactRequest request = new ContactRequest("Jane", "9842642268", "jane@example.com", null, "");

		Set<ConstraintViolation<ContactRequest>> violations = validator.validate(request);

		assertEquals(2, violations.size());
	}

	@Test
	void invalidPhoneIsRejected() {
		ContactRequest request = new ContactRequest("Jane", "call me", "jane@example.com", "Wedding", "Hello");

		Set<ConstraintViolation<ContactRequest>> violations = validator.validate(request);

		assertEquals(1, violations.size());
		assertEquals("phone", violations.iterator().next().getPropertyPath().toString());
	}

	@Test
	void registerRejectsShortPassword() {
		RegisterRequest request = new RegisterRequest("Jane", "jane@example.com", "short");

		Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

		assertEquals(1, violations.size());
		assertEquals("password", violations.iterator().next().getPropertyPath().toString());
	}
}
