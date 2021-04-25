package com.dcascos.motogo.models;

public class Comment {

	private String id;
	private long creationDate;
	private String commentText;
	private String userId;
	private String postId;

	public Comment() {

	}

	public Comment(String id, long creationDate, String commentText, String userId, String postId) {
		this.id = id;
		this.creationDate = creationDate;
		this.commentText = commentText;
		this.userId = userId;
		this.postId = postId;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
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
