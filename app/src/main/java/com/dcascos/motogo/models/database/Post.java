package com.dcascos.motogo.models.database;

public class Post {

	private String id;
	private String title;
	private String location;
	private String description;
	private String image;
	private String userId;
	private long creationDate;

	public Post() {

	}

	public Post(String id, String title, String location, String description, String image, String userId, long creationDate) {
		this.id = id;
		this.title = title;
		this.location = location;
		this.description = description;
		this.image = image;
		this.userId = userId;
		this.creationDate = creationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}
