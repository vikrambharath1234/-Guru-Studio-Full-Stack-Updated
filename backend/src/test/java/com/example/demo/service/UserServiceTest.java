package com.example.demo.service;

import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	private UserService userService;

	@BeforeEach
	void setUp() {
		userService = new UserService(userRepository);
	}

	@Test
	void getProfileReturnsUserWithoutPasswordHash() {
		User user = new User("Jane Doe", "jane@example.com", "hash-value", Role.USER);
		when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

		UserResponse response = userService.getProfile("jane@example.com");

		assertEquals("Jane Doe", response.fullName());
		assertEquals("jane@example.com", response.email());
	}

	@Test
	void getProfileForMissingUserIsNotFound() {
		when(userRepository.findByEmail("ghost@example.com")).thenReturn(Optional.empty());

		ApiException ex = assertThrows(ApiException.class,
				() -> userService.getProfile("ghost@example.com"));

		assertEquals(HttpStatus.NOT_FOUND, ex.getStatus());
	}

	@Test
	void updateProfileChangesNameAndPhone() {
		User user = new User("Jane Doe", "jane@example.com", "hash-value", Role.USER);
		when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

		UserResponse response = userService.updateProfile("jane@example.com",
				new UpdateProfileRequest("  Jane Smith ", " 0700 123 456 "));

		assertEquals("Jane Smith", response.fullName());
		assertEquals("0700 123 456", response.phone());
		assertEquals("USER", response.role());
	}

	@Test
	void updateProfileWithBlankPhoneStoresNull() {
		User user = new User("Jane Doe", "jane@example.com", "hash-value", Role.USER);
		user.setPhone("0700 000 000");
		when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

		UserResponse response = userService.updateProfile("jane@example.com",
				new UpdateProfileRequest("Jane Doe", "   "));

		assertNull(response.phone());
	}
}
