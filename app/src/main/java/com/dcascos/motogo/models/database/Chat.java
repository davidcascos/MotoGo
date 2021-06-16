package com.dcascos.motogo.models.database;

public class Chat {

	private String userId1;
	private String userId2;
	private boolean isWriting;
	private long creationDate;

	public Chat() {

	}

	public Chat(String userId1, String userId2, boolean isWriting, long creationDate) {
		this.userId1 = userId1;
		this.userId2 = userId2;
		this.isWriting = isWriting;
		this.creationDate = creationDate;
	}

	public String getUserId1() {
		return userId1;
	}

	public void setUserId1(String userId1) {
		this.userId1 = userId1;
	}

	public String getUserId2() {
		return userId2;
	}

	public void setUserId2(String userId2) {
		this.userId2 = userId2;
	}

	public boolean isWriting() {
		return isWriting;
	}

	public void setWriting(boolean writing) {
		isWriting = writing;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}
