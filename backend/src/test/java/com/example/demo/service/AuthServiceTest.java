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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	private static final String SECRET = "test-secret-key-that-is-at-least-32-bytes-long!!";

	@Mock
	private UserRepository userRepository;

	private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	private final JwtService jwtService = new JwtService(SECRET, 60);

	private AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, jwtService);
	}

	@Test
	void registerStoresBcryptHashNotThePlainPassword() {
		when(userRepository.existsByEmail("jane@example.com")).thenReturn(false);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		UserResponse response = authService.register(
				new RegisterRequest("  Jane Doe ", "Jane@Example.com", "secret123"));

		ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(captor.capture());
		User saved = captor.getValue();

		assertNotEquals("secret123", saved.getPasswordHash());
		assertTrue(passwordEncoder.matches("secret123", saved.getPasswordHash()));
		assertEquals("jane@example.com", saved.getEmail());
		assertEquals("Jane Doe", saved.getFullName());
		assertEquals(Role.USER, saved.getRole());
		assertEquals("jane@example.com", response.email());
		assertEquals("USER", response.role());
	}

	@Test
	void registerRejectsDuplicateEmailWithConflict() {
		when(userRepository.existsByEmail("jane@example.com")).thenReturn(true);

		ApiException ex = assertThrows(ApiException.class,
				() -> authService.register(new RegisterRequest("Jane", "jane@example.com", "secret123")));

		assertEquals(HttpStatus.CONFLICT, ex.getStatus());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void loginWithCorrectPasswordReturnsToken() {
		User user = new User("Jane Doe", "jane@example.com", passwordEncoder.encode("secret123"), Role.USER);
		when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

		AuthResponse response = authService.login(new LoginRequest("JANE@example.com", "secret123"));

		assertEquals("Bearer", response.tokenType());
		assertEquals(60, response.expiresInMinutes());
		assertFalse(response.accessToken().isBlank());
		assertEquals("jane@example.com", jwtService.parseClaims(response.accessToken()).getSubject());
	}

	@Test
	void loginWithWrongPasswordIsUnauthorized() {
		User user = new User("Jane Doe", "jane@example.com", passwordEncoder.encode("secret123"), Role.USER);
		when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

		ApiException ex = assertThrows(ApiException.class,
				() -> authService.login(new LoginRequest("jane@example.com", "wrong-password")));

		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
		assertEquals("Invalid email or password.", ex.getMessage());
	}

	@Test
	void loginWithUnknownEmailIsUnauthorizedWithSameMessage() {
		when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class,
				() -> authService.login(new LoginRequest("nobody@example.com", "secret123")));

		assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatus());
		assertEquals("Invalid email or password.", ex.getMessage());
	}
}
