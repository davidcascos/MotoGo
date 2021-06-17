package com.dcascos.motogo.models.database;

public class User {

	private String id;
	private String fullName;
	private String username;
	private String email;
	private String imageCover;
	private String imageProfile;
	private long creationDate;
	private long modificationDate;
	private boolean online;
	private long onlineLastDate;

	public User() {

	}

	public User(String id, String fullName, String username, String email, String imageCover, String imageProfile, long creationDate, long modificationDate, boolean online, long onlineLastDate) {
		this.id = id;
		this.fullName = fullName;
		this.username = username;
		this.email = email;
		this.imageCover = imageCover;
		this.imageProfile = imageProfile;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
		this.online = online;
		this.onlineLastDate = onlineLastDate;
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

	public String getImageCover() {
		return imageCover;
	}

	public void setImageCover(String imageCover) {
		this.imageCover = imageCover;
	}

	public String getImageProfile() {
		return imageProfile;
	}

	public void setImageProfile(String imageProfile) {
		this.imageProfile = imageProfile;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public long getModificationDate() {
		return modificationDate;
	}

	public void setModificationDate(long modificationDate) {
		this.modificationDate = modificationDate;
	}

	public boolean isOnline() {
		return online;
	}

	public void setOnline(boolean online) {
		this.online = online;
	}

	public long getOnlineLastDate() {
		return onlineLastDate;
	}

	public void setOnlineLastDate(long onlineLastDate) {
		this.onlineLastDate = onlineLastDate;
	}
}
