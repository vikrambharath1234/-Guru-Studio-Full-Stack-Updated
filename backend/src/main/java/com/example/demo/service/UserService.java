package com.example.demo.service;

import com.example.demo.dto.UpdateProfileRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.exception.ApiException;
import com.example.demo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Reading and updating the logged-in user's own profile. */
@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional(readOnly = true)
	public UserResponse getProfile(String email) {
		return UserResponse.from(findByEmail(email));
	}

	/** Changes only the name and phone. Email, role and password are not changed here. */
	@Transactional
	public UserResponse updateProfile(String email, UpdateProfileRequest request) {
		User user = findByEmail(email);
		user.setFullName(request.fullName().trim());
		user.setPhone(blankToNull(request.phone()));
		return UserResponse.from(user);
	}

	private User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found."));
	}

	private static String blankToNull(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
