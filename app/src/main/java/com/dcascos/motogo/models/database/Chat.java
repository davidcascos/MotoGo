package com.dcascos.motogo.models.database;

import java.util.ArrayList;

public class Chat {

	private String id;
	private String userId1;
	private String userId2;
	private boolean isWriting;
	private ArrayList<String> listUsersIds;
	private long creationDate;
	private long modificationDate;

	public Chat() {

	}

	public Chat(String id, long modificationDate) {
		this.id = id;
		this.modificationDate = modificationDate;
	}

	public Chat(String id, String userId1, String userId2, boolean isWriting, ArrayList<String> ids, long creationDate, long modificationDate) {
		this.id = id;
		this.userId1 = userId1;
		this.userId2 = userId2;
		this.isWriting = isWriting;
		this.listUsersIds = ids;
		this.creationDate = creationDate;
		this.modificationDate = modificationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public ArrayList<String> getListUsersIds() {
		return listUsersIds;
	}

	public void setListUsersIds(ArrayList<String> listUsersIds) {
		this.listUsersIds = listUsersIds;
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
}
