package com.example.demo.exception;

import com.example.demo.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Turns every error into the same JSON shape (ApiErrorResponse).
 * Stack traces and database details are logged on the server, never sent to the browser.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	/** Errors we raise on purpose, such as "email already used" (409). */
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiErrorResponse> handleApiException(ApiException ex, HttpServletRequest request) {
		return build(ex.getStatus(), ex.getMessage(), null, request);
	}

	/** Validation failed (for example, an invalid email). Lists the bad fields. */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
		Map<String, String> fieldErrors = new LinkedHashMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
		}
		return build(HttpStatus.BAD_REQUEST, "Validation failed. Please check the highlighted fields.", fieldErrors, request);
	}

	/** The request body is not valid JSON, or is missing. */
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex, HttpServletRequest request) {
		return build(HttpStatus.BAD_REQUEST, "The request body is missing or is not valid JSON.", null, request);
	}

	/** A photo is bigger than the limit set in application.properties. */
	@ExceptionHandler(MaxUploadSizeExceededException.class)
	public ResponseEntity<ApiErrorResponse> handleFileTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
		return build(HttpStatus.PAYLOAD_TOO_LARGE, "The file is too large. Photos must be 10 MB or smaller.", null, request);
	}

	/** A database rule was broken, for example a duplicate email reached the database at the same moment. */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex, HttpServletRequest request) {
		log.warn("Data integrity problem on {} {}", request.getMethod(), request.getRequestURI(), ex);
		return build(HttpStatus.CONFLICT, "This data conflicts with an existing record.", null, request);
	}

	/**
	 * Everything else. Spring's own 4xx errors (such as an unknown URL) keep their status code.
	 * Anything unexpected becomes a generic 500.
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiErrorResponse> handleOther(Exception ex, HttpServletRequest request) {
		if (ex instanceof ErrorResponse errorResponse) {
			HttpStatus status = HttpStatus.resolve(errorResponse.getStatusCode().value());
			if (status != null && status.is4xxClientError()) {
				return build(status, "The request could not be processed.", null, request);
			}
		}
		log.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), ex);
		return build(HttpStatus.INTERNAL_SERVER_ERROR, "Something went wrong. Please try again later.", null, request);
	}

	private ResponseEntity<ApiErrorResponse> build(HttpStatus status, String message,
			Map<String, String> fieldErrors, HttpServletRequest request) {
		ApiErrorResponse body = new ApiErrorResponse(
				Instant.now(),
				status.value(),
				status.getReasonPhrase(),
				message,
				request.getRequestURI(),
				fieldErrors == null ? Map.of() : fieldErrors);
		return ResponseEntity.status(status).body(body);
	}
}
