package com.dcascos.motogo.database;

public class UserHelper {

	String fullName, username, email;

	public UserHelper() {
	}

	public UserHelper(String fullName, String username, String email) {
		this.fullName = fullName;
		this.username = username;
		this.email = email;
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
