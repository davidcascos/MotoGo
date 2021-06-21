package com.dcascos.motogo.models.database;

public class Score {
	private String id;
	private String routeId;
	private String userId;
	private float scoreNumber;
	private long creationDate;

	public Score() {

	}

	public Score(String id, String routeId, String userId, float scoreNumber, long creationDate) {
		this.id = id;
		this.routeId = routeId;
		this.userId = userId;
		this.scoreNumber = scoreNumber;
		this.creationDate = creationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public float getScoreNumber() {
		return scoreNumber;
	}

	public void setScoreNumber(float scoreNumber) {
		this.scoreNumber = scoreNumber;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}


