package com.example.demo.exception;

import org.springframework.http.HttpStatus;

/**
 * An error with a known HTTP status, such as 404 or 409.
 * The message is shown to the user, so do not put secrets or internal details in it.
 */
public class ApiException extends RuntimeException {

	private final HttpStatus status;

	public ApiException(HttpStatus status, String message) {
		super(message);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}
}
