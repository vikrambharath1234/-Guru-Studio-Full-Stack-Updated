
package com.example.demo.config;

import com.example.demo.security.JwtAuthenticationFilter;
import com.example.demo.security.JwtService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(
			HttpSecurity http,
			JwtService jwtService) throws Exception {

		http
				.csrf(csrf -> csrf.disable())

				.cors(Customizer.withDefaults())

				.sessionManagement(session ->
						session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				)

				.httpBasic(basic -> basic.disable())

				.formLogin(form -> form.disable())

				.exceptionHandling(ex ->
						ex.authenticationEntryPoint(
								new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
						)
				)

				.authorizeHttpRequests(auth -> auth

						// Allow browser CORS preflight requests
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						// Public root URL
						.requestMatchers("/").permitAll()

						// Public authentication APIs
						.requestMatchers("/api/auth/**").permitAll()

						// Public POST APIs
						.requestMatchers(
								HttpMethod.POST,
								"/api/contact",
								"/api/bookings",
								"/api/reviews"
						).permitAll()

						// Public GET APIs
						.requestMatchers(
								HttpMethod.GET,
								"/api/gallery",
								"/api/reviews"
						).permitAll()

						.requestMatchers("/error").permitAll()

						// Admin APIs require ADMIN role
						.requestMatchers("/api/admin/**").hasRole("ADMIN")

						// Everything else requires authentication
						.anyRequest().authenticated()
				)

				.addFilterBefore(
						new JwtAuthenticationFilter(jwtService),
						UsernamePasswordAuthenticationFilter.class
				);

		return http.build();
	}
}
