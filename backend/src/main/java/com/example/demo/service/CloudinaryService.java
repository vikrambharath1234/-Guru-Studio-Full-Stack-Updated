package com.example.demo.service;

import com.example.demo.exception.ApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;

/**
 * Sends photos to Cloudinary (an image storage service) and deletes them again.
 *
 * Cloudinary needs a "signature" with every upload or delete. The signature is a SHA-1
 * hash of the settings plus your secret. The secret is never sent, only the hash.
 * Credentials come from environment variables (see application.properties).
 */
@Service
public class CloudinaryService {

	private static final Logger log = LoggerFactory.getLogger(CloudinaryService.class);
	private static final String API_BASE = "https://api.cloudinary.com/v1_1/";

	private final String cloudName;
	private final String apiKey;
	private final String apiSecret;
	private final RestClient restClient = RestClient.create();

	public CloudinaryService(
			@Value("${cloudinary.cloud-name:}") String cloudName,
			@Value("${cloudinary.api-key:}") String apiKey,
			@Value("${cloudinary.api-secret:}") String apiSecret) {
		this.cloudName = cloudName;
		this.apiKey = apiKey;
		this.apiSecret = apiSecret;
	}

	/** Uploads one photo into the folder "guru-studio/<category>". Returns its web address and id. */
	public CloudinaryImage upload(MultipartFile file, String category) {
		requireConfigured();
		long timestamp = System.currentTimeMillis() / 1000;
		String folder = "guru-studio/" + category;

		// Parameters must be sorted by name (folder, then timestamp) before signing
		String signature = sha1("folder=" + folder + "&timestamp=" + timestamp + apiSecret);

		MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
		form.add("file", toResource(file));
		form.add("api_key", apiKey);
		form.add("timestamp", String.valueOf(timestamp));
		form.add("folder", folder);
		form.add("signature", signature);

		try {
			Map<?, ?> response = restClient.post()
					.uri(API_BASE + cloudName + "/image/upload")
					.contentType(MediaType.MULTIPART_FORM_DATA)
					.body(form)
					.retrieve()
					.body(Map.class);

			if (response == null || response.get("secure_url") == null || response.get("public_id") == null) {
				throw new ApiException(HttpStatus.BAD_GATEWAY, "Image upload failed. Please try again.");
			}
			return new CloudinaryImage(
					(String) response.get("secure_url"),
					(String) response.get("public_id"));
		} catch (RestClientException ex) {
			log.error("Cloudinary upload failed", ex);
			throw new ApiException(HttpStatus.BAD_GATEWAY, "Image upload failed. Check the Cloudinary settings.");
		}
	}

	/** Deletes one photo from Cloudinary using its public id. */
	public void delete(String publicId) {
		requireConfigured();
		long timestamp = System.currentTimeMillis() / 1000;
		String signature = sha1("public_id=" + publicId + "&timestamp=" + timestamp + apiSecret);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("public_id", publicId);
		form.add("api_key", apiKey);
		form.add("timestamp", String.valueOf(timestamp));
		form.add("signature", signature);

		try {
			restClient.post()
					.uri(API_BASE + cloudName + "/image/destroy")
					.contentType(MediaType.APPLICATION_FORM_URLENCODED)
					.body(form)
					.retrieve()
					.toBodilessEntity();
		} catch (RestClientException ex) {
			log.error("Cloudinary delete failed for {}", publicId, ex);
			throw new ApiException(HttpStatus.BAD_GATEWAY, "Could not delete the photo from image storage.");
		}
	}

	private void requireConfigured() {
		if (cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) {
			throw new ApiException(HttpStatus.SERVICE_UNAVAILABLE,
					"Image storage is not configured yet. Set the CLOUDINARY_* environment variables.");
		}
	}

	/** Wraps the uploaded bytes so Cloudinary receives them with the original file name. */
	private static ByteArrayResource toResource(MultipartFile file) {
		try {
			String name = file.getOriginalFilename() == null ? "photo.jpg" : file.getOriginalFilename();
			return new ByteArrayResource(file.getBytes()) {
				@Override
				public String getFilename() {
					return name;
				}
			};
		} catch (IOException ex) {
			throw new ApiException(HttpStatus.BAD_REQUEST, "Could not read one of the uploaded photos.");
		}
	}

	/** SHA-1 hash written as hex text, which is what Cloudinary expects. */
	private static String sha1(String text) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			return HexFormat.of().formatHex(digest.digest(text.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-1 is not available on this Java installation.", ex);
		}
	}

	/** The two values we keep from a successful upload. */
	public record CloudinaryImage(String url, String publicId) {
	}
}
