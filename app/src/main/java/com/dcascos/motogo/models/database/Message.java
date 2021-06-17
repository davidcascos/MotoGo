package com.dcascos.motogo.models.database;

public class Message {

	private String id;
	private String userIdSender;
	private String userIdReciver;
	private String chatId;
	private String messageText;
	private boolean messageViewed;
	private long creationDate;

	public Message() {

	}

	public Message(String id, String userIdSender, String userIdReciver, String chatId, String messageText, boolean messageViewed, long creationDate) {
		this.id = id;
		this.userIdSender = userIdSender;
		this.userIdReciver = userIdReciver;
		this.chatId = chatId;
		this.messageText = messageText;
		this.messageViewed = messageViewed;
		this.creationDate = creationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserIdSender() {
		return userIdSender;
	}

	public void setUserIdSender(String userIdSender) {
		this.userIdSender = userIdSender;
	}

	public String getUserIdReciver() {
		return userIdReciver;
	}

	public void setUserIdReciver(String userIdReciver) {
		this.userIdReciver = userIdReciver;
	}

	public String getChatId() {
		return chatId;
	}

	public void setChatId(String chatId) {
		this.chatId = chatId;
	}

	public String getMessageText() {
		return messageText;
	}

	public void setMessageText(String messageText) {
		this.messageText = messageText;
	}

	public boolean isMessageViewed() {
		return messageViewed;
	}

	public void setMessageViewed(boolean messageViewed) {
		this.messageViewed = messageViewed;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}
