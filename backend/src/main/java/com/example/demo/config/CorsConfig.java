package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * Lets the React frontend call this API from a different address (CORS).
 * Allowed origins: http://localhost:5173 plus the FRONTEND_URL environment variable.
 * A wildcard (*) is never used, because the browser would block it with credentials.
 */
@Configuration
public class CorsConfig {

	private static final String LOCAL_FRONTEND = "http://localhost:5173";

	@Bean
	public CorsConfigurationSource corsConfigurationSource(@Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
		List<String> allowedOrigins = new ArrayList<>();
		allowedOrigins.add(LOCAL_FRONTEND);
		if (frontendUrl != null && !frontendUrl.isBlank()) {
			String trimmed = frontendUrl.trim();
			if (!allowedOrigins.contains(trimmed)) {
				allowedOrigins.add(trimmed);
			}
		}

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(allowedOrigins);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		// Login uses a Bearer token in a header, not cookies, so credentials are not needed.
		config.setAllowCredentials(false);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		return source;
	}
}
