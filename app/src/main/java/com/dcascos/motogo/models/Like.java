package com.dcascos.motogo.models;

public class Like {
	private String id;
	private String postId;
	private String userId;
	private long creationDate;

	public Like() {

	}

	public Like(String id, String postId, String userId, long creationDate) {
		this.id = id;
		this.postId = postId;
		this.userId = userId;
		this.creationDate = creationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPostId() {
		return postId;
	}

	public void setPostId(String postId) {
		this.postId = postId;
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
