package com.dcascos.motogo.models;

public class Comment {

	private String id;
	private String creationDate;
	private String comment;
	private String userId;
	private String postId;

	public Comment() {

	}

	public Comment(String id, String creationDate, String comment, String userId, String postId) {
		this.id = id;
		this.creationDate = creationDate;
		this.comment = comment;
		this.userId = userId;
		this.postId = postId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
}
