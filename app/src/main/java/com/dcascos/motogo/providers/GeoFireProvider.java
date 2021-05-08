package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.maps.model.LatLng;

public class GeoFireProvider {

	private DatabaseReference databaseReference;
	private GeoFire geoFire;

	public GeoFireProvider() {
		databaseReference = FirebaseDatabase.getInstance().getReference(Constants.MAPS_ACTIVEDRIVERS);
		geoFire = new GeoFire(databaseReference);
	}

	public void saveLocation(String userId, LatLng latLng) {
		geoFire.setLocation(userId, new GeoLocation(latLng.latitude, latLng.longitude));
	}

	public void deleteLocation(String userId) {
		geoFire.removeLocation(userId);
	}

	public GeoQuery getActiveDrivers(LatLng latLng, Long radius) {
		GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(latLng.latitude, latLng.longitude), radius);
		geoQuery.removeAllListeners();
		return geoQuery;
	}

}
