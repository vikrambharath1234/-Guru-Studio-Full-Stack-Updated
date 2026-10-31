package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * Creates (or updates) the single admin account when the app starts.
 *
 * There is no public admin sign-up. The admin's login name and password come from the
 * ADMIN_USERNAME and ADMIN_PASSWORD environment variables. The password is stored only as a
 * BCrypt hash. Every startup refreshes the hash, so changing ADMIN_PASSWORD and restarting
 * is enough to change the password.
 */
@Component
public class AdminAccountInitializer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(AdminAccountInitializer.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final String adminUsername;
	private final String adminPassword;

	public AdminAccountInitializer(UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			@Value("${app.admin.username:}") String adminUsername,
			@Value("${app.admin.password:}") String adminPassword) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.adminUsername = adminUsername;
		this.adminPassword = adminPassword;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		if (adminUsername.isBlank() || adminPassword.isBlank()) {
			log.warn("ADMIN_USERNAME or ADMIN_PASSWORD is not set. The admin login will not work until both are set.");
			return;
		}
		if (adminPassword.length() < 8 || adminPassword.length() > 72) {
			log.error("ADMIN_PASSWORD must be 8 to 72 characters. The admin account was not created.");
			return;
		}

		// Stored lowercase, because login also lowercases the name
		String username = adminUsername.trim().toLowerCase(Locale.ROOT);
		String passwordHash = passwordEncoder.encode(adminPassword);

		User admin = userRepository.findByEmail(username).orElse(null);
		if (admin == null) {
			userRepository.save(new User("Studio Admin", username, passwordHash, Role.ADMIN));
		} else {
			// The account already exists (maybe from an old sign-up): make sure it is the admin
			admin.setPasswordHash(passwordHash);
			admin.setRole(Role.ADMIN);
		}
		log.info("Admin account is ready.");
	}
}
