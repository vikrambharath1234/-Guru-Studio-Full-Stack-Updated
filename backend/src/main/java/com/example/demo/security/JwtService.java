package com.example.demo.security;

import com.example.demo.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

/**
 * Creates and checks JWT login tokens (signed with HMAC-SHA256).
 * A token holds the user's email (subject), their role, and an expiry time.
 */
@Service
public class JwtService {

	private static final int MIN_SECRET_BYTES = 32;

	private final SecretKey signingKey;
	private final long expirationMinutes;

	public JwtService(
			@Value("${app.jwt.secret}") String secret,
			@Value("${app.jwt.expiration-minutes:60}") long expirationMinutes) {

		if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < MIN_SECRET_BYTES) {
			throw new IllegalStateException(
					"JWT_SECRET must be at least 32 characters long. Set it as an environment variable.");
		}
		if (expirationMinutes <= 0) {
			throw new IllegalStateException("JWT expiration must be more than 0 minutes.");
		}
		this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
		this.expirationMinutes = expirationMinutes;
	}

	/** Creates a signed token for this user. */
	public String generateToken(User user) {
		Instant now = Instant.now();
		return Jwts.builder()
				.subject(user.getEmail())
				.claim("role", user.getRole().name())
				.issuedAt(Date.from(now))
				.expiration(Date.from(now.plus(Duration.ofMinutes(expirationMinutes))))
				.signWith(signingKey)
				.compact();
	}

	/**
	 * Checks the signature and expiry, then returns the claims.
	 * Throws a JwtException (a RuntimeException) if the token is invalid or expired.
	 */
	public Claims parseClaims(String token) {
		return Jwts.parser()
				.verifyWith(signingKey)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public long getExpirationMinutes() {
		return expirationMinutes;
	}
}
