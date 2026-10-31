package com.example.demo.dto;

/**
 * Returned after a successful login.
 * The frontend must send accessToken in the header: Authorization: Bearer <accessToken>
 */
public record AuthResponse(String accessToken, String tokenType, long expiresInMinutes) {
}
