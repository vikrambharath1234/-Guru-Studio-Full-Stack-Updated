package com.example.demo.security;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

	private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long!!";

	private final JwtService jwtService = new JwtService(SECRET, 60);

	@Test
	void generatedTokenContainsEmailAndRole() {
		User user = new User("Jane Doe", "jane@example.com", "unused-hash", Role.USER);

		String token = jwtService.generateToken(user);
		Claims claims = jwtService.parseClaims(token);

		assertFalse(token.isBlank());
		assertEquals("jane@example.com", claims.getSubject());
		assertEquals("USER", claims.get("role", String.class));
		assertTrue(claims.getExpiration().after(claims.getIssuedAt()));
	}

	@Test
	void tokenWithChangedSignatureIsRejected() {
		User user = new User("Jane Doe", "jane@example.com", "unused-hash", Role.USER);
		String token = jwtService.generateToken(user);

		String tampered = token.substring(0, token.lastIndexOf('.') + 1) + "invalidsignature";

		assertThrows(JwtException.class, () -> jwtService.parseClaims(tampered));
	}

	@Test
	void tokenSignedWithAnotherSecretIsRejected() {
		JwtService otherService = new JwtService("another-secret-key-that-is-also-32-bytes-long!!", 60);
		User user = new User("Jane Doe", "jane@example.com", "unused-hash", Role.USER);
		String foreignToken = otherService.generateToken(user);

		assertThrows(JwtException.class, () -> jwtService.parseClaims(foreignToken));
	}

	@Test
	void shortSecretIsRefusedAtStartup() {
		assertThrows(IllegalStateException.class, () -> new JwtService("too-short", 60));
	}
}
