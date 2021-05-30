package com.dcascos.motogo.models.database;

public class Comment {

	private String id;
	private String text;
	private String userId;
	private String postId;
	private long creationDate;

	public Comment() {

	}

	public Comment(String id, String text, String userId, String postId, long creationDate) {
		this.id = id;
		this.text = text;
		this.userId = userId;
		this.postId = postId;
		this.creationDate = creationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}
