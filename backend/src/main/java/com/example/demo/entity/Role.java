package com.example.demo.entity;

/**
 * The permission level of an account.
 * New accounts are always USER. ADMIN can only be set manually in the database.
 */
public enum Role {
	USER,
	ADMIN
}
