package com.dcascos.motogo.models.database;

public class Route {

	private String id;
	private String userId;
	private String origin;
	private String destination;
	private String distance;
	private String duration;
	private double originLat;
	private double originLon;
	private double destinationLat;
	private double destinationLon;
	private String points;
	private long creationDate;

	public Route() {

	}

	public Route(String id, String userId, String origin, String destination, String distance, String duration, double originLat, double originLon, double destinationLat, double destinationLon, String points, long creationDate) {
		this.id = id;
		this.userId = userId;
		this.origin = origin;
		this.destination = destination;
		this.distance = distance;
		this.duration = duration;
		this.originLat = originLat;
		this.originLon = originLon;
		this.destinationLat = destinationLat;
		this.destinationLon = destinationLon;
		this.points = points;
		this.creationDate = creationDate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public double getOriginLat() {
		return originLat;
	}

	public void setOriginLat(double originLat) {
		this.originLat = originLat;
	}

	public double getOriginLon() {
		return originLon;
	}

	public void setOriginLon(double originLon) {
		this.originLon = originLon;
	}

	public double getDestinationLat() {
		return destinationLat;
	}

	public void setDestinationLat(double destinationLat) {
		this.destinationLat = destinationLat;
	}

	public double getDestinationLon() {
		return destinationLon;
	}

	public void setDestinationLon(double destinationLon) {
		this.destinationLon = destinationLon;
	}

	public String getPoints() {
		return points;
	}

	public void setPoints(String points) {
		this.points = points;
	}

	public long getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(long creationDate) {
		this.creationDate = creationDate;
	}
}
