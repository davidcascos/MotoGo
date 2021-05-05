package com.dcascos.motogo.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.GeoFireProvider;
import com.dcascos.motogo.utils.PermissionUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

	private View view;
	private GoogleMap mMap;
	private SupportMapFragment mapFragment;

	private LocationRequest locationRequest;
	private FusedLocationProviderClient fusedLocationProviderClient;

	private LatLng currentLatLong;

	private AuthProvider authProvider;
	private GeoFireProvider geoFireProvider;

	private LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(@NonNull LocationResult locationResult) {
			super.onLocationResult(locationResult);

			for (Location location : locationResult.getLocations()) {
				if (getContext() != null) {
					currentLatLong = new LatLng(location.getLatitude(), location.getLongitude());

					mMap.moveCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15).build()));

					updateLocation();
				}
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_maps, container, false);

		mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		authProvider = new AuthProvider();
		geoFireProvider = new GeoFireProvider();

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

		return view;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		locationRequest = LocationRequest.create()
				.setInterval(1000)
				.setFastestInterval(1000)
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setSmallestDisplacement(5);

		checkVersionToStartLocation();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);

		if (requestCode == Constants.REQUEST_CODE_LOCATION
				&& grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
				&& ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			checlAllForLocation();
		}
	}

	private void checlAllForLocation() {
		if (itsGpsActived()) {
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
			mMap.setMyLocationEnabled(true);
		} else {
			showAlertDialogNoGps();
		}
	}

	private void checkVersionToStartLocation() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
				checlAllForLocation();
			} else {
				checkLocationPermissions();
			}
		} else {
			checlAllForLocation();
		}
	}

	private void checkLocationPermissions() {
		if (!PermissionUtils.hasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
			if (PermissionUtils.shouldShowRational(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
				new AlertDialog.Builder(getContext()).setTitle(R.string.permissionLocationTitle).setMessage(getContext().getText(R.string.permissionLocation)).setPositiveButton("OK", (dialog, which) ->
						PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION)).create().show();
			} else {
				PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION);
			}
		}
	}

	private void showAlertDialogNoGps() {
		AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

		builder.setMessage(R.string.pleaseActiveLocation).setPositiveButton("Settings", (dialog, which) ->
				startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.REQUEST_CODE_SETTINGS)).create().show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_CODE_SETTINGS && itsGpsActived()) {
			if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
				return;
			}
			fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
			mMap.setMyLocationEnabled(true);
		} else {
			showAlertDialogNoGps();
		}
	}

	private boolean itsGpsActived() {
		LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			return true;
		} else {
			geoFireProvider.deleteLocation(authProvider.getUserId());
			return false;
		}
	}

	private void updateLocation() {
		if (currentLatLong != null) {
			geoFireProvider.saveLocation(authProvider.getUserId(), currentLatLong);
		}
	}

}