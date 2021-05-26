package com.dcascos.motogo.providers;

import android.content.Context;

import com.dcascos.motogo.R;
import com.dcascos.motogo.retrofit.IGoogleApi;
import com.dcascos.motogo.retrofit.RetrofitClient;
import com.google.android.gms.maps.model.LatLng;

import retrofit2.Call;

public class GoogleAPIProvider {

	public GoogleAPIProvider(Context context) {
		this.context = context;
	}

	private Context context;

	public Call<String> getDirections(LatLng originLatLong, LatLng destinationLatLong) {
		String baseUrl = "https://maps.googleapis.com";

		String query = "/maps/api/directions/json?mode=driving&transit_routing_preferences=less_driving&"
				+ "origin=" + originLatLong.latitude + "," + originLatLong.longitude + "&"
				+ "destination=" + destinationLatLong.latitude + "," + destinationLatLong.longitude + "&"
				+ "key=" + context.getResources().getString(R.string.google_api_key);

		return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class).getDirections(baseUrl + query);

	}
}
