package com.example.demo.controller;

import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Protected endpoints for the logged-in user's own profile.
 * These need the header: Authorization: Bearer <token>
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	/** GET /api/users/me -> the logged-in user's profile. */
	@GetMapping("/me")
	public UserResponse getMyProfile(Authentication authentication) {
		return userService.getProfile(authentication.getName());
	}

	/** PUT /api/users/me -> updates name and phone only. */
	@PutMapping("/me")
	public UserResponse updateMyProfile(Authentication authentication,
			@Valid @RequestBody UpdateProfileRequest request) {
		return userService.updateProfile(authentication.getName(), request);
	}
}
