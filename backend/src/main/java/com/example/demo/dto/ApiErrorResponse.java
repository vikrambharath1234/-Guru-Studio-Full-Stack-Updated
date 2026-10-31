package com.example.demo.dto;

import java.time.Instant;
import java.util.Map;

/**
 * The JSON body returned for every error.
 * Example: { "status": 409, "error": "Conflict", "message": "...", "fieldErrors": {} }
 */
public record ApiErrorResponse(
		Instant timestamp,
		int status,
		String error,
		String message,
		String path,
		Map<String, String> fieldErrors) {
}
