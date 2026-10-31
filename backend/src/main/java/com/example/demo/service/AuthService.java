package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/** Registration and login. */
@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.jwtService = jwtService;
	}

	/** Creates a new USER account. The role is always USER here, whatever the request says. */
	@Transactional
	public UserResponse register(RegisterRequest request) {
		String email = normalizeEmail(request.email());

		if (userRepository.existsByEmail(email)) {
			throw new ApiException(HttpStatus.CONFLICT, "An account with this email already exists.");
		}

		User user = new User(
				request.fullName().trim(),
				email,
				passwordEncoder.encode(request.password()),
				Role.USER);

		return UserResponse.from(userRepository.save(user));
	}

	/**
	 * Checks the email and password. If both are correct, returns a JWT token.
	 * The error message is the same for a wrong email and a wrong password,
	 * so attackers cannot find out which emails have accounts.
	 */
	@Transactional(readOnly = true)
	public AuthResponse login(LoginRequest request) {
		String email = normalizeEmail(request.email());

		User user = userRepository.findByEmail(email)
				.orElseThrow(AuthService::invalidCredentials);

		if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
			throw invalidCredentials();
		}

		return new AuthResponse(
				jwtService.generateToken(user),
				"Bearer",
				jwtService.getExpirationMinutes());
	}

	private static ApiException invalidCredentials() {
		return new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password.");
	}

	/** Emails are trimmed and lowercased, so " Jane@Mail.com " matches "jane@mail.com". */
	static String normalizeEmail(String email) {
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
