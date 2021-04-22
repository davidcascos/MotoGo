package com.dcascos.motogo.models;

public class User {

	private String id;
	private String fullName;
	private String username;
	private String email;

	public User() {
	}

	public User(String id, String fullName, String username, String email) {
		this.id = id;
		this.fullName = fullName;
		this.username = username;
		this.email = email;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
