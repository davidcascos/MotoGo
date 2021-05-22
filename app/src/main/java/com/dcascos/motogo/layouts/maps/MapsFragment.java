package com.dcascos.motogo.layouts.maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.GeoFireProvider;
import com.dcascos.motogo.utils.MapPreferences;
import com.dcascos.motogo.utils.PermissionUtils;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

	private View view;
	private Toolbar toolbar;

	private boolean showMyLocation;
	private boolean showOthers;
	private double distanceRadius;

	private GoogleMap mMap;
	private SupportMapFragment mapFragment;

	private LocationRequest locationRequest;
	private FusedLocationProviderClient fusedLocationProviderClient;

	private LatLng currentLatLong;
	private LatLng destinationLatLong;

	private AuthProvider authProvider;
	private GeoFireProvider geoFireProvider;

	private Marker meMarker;
	private List<Marker> driversMarkers = new ArrayList<>();

	private AutocompleteSupportFragment autocompleteSupportFragment;
	private PlacesClient placesClient;
	private String destinationName;

	private Button btCreateRoute;

	private LocationCallback locationCallback = new LocationCallback() {
		@Override
		public void onLocationResult(@NonNull LocationResult locationResult) {
			super.onLocationResult(locationResult);

			for (Location location : locationResult.getLocations()) {
				if (view.getContext() != null) {

					if (meMarker != null) {
						meMarker.remove();
					}

					meMarker = mMap.addMarker(new MarkerOptions()
							.position(new LatLng(location.getLatitude(), location.getLongitude()))
							.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto_me))
							.zIndex(1));

					currentLatLong = new LatLng(location.getLatitude(), location.getLongitude());
					checkActiveDrivers();

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
		getPreferences();

		toolbar = view.findViewById(R.id.toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.empty));
		setHasOptionsMenu(true);

		mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		authProvider = new AuthProvider();
		geoFireProvider = new GeoFireProvider();

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

		autocompleteDestination();

		btCreateRoute = view.findViewById(R.id.bt_createRoute);

		btCreateRoute.setOnClickListener(v -> createRoute());

		return view;
	}

	private void createRoute() {
		if (currentLatLong != null && destinationLatLong != null) {
			Intent intent = new Intent(getContext(), RouteDetail.class);
			intent.putExtra("currentLat", currentLatLong.latitude);
			intent.putExtra("currentLon", currentLatLong.longitude);
			intent.putExtra("destinationLat", destinationLatLong.latitude);
			intent.putExtra("destinationLon", destinationLatLong.longitude);

			startActivity(intent);
		} else {
			Toast.makeText(getContext(), R.string.selectDestination, Toast.LENGTH_SHORT).show();
		}
	}

	private void autocompleteDestination() {
		if (!Places.isInitialized()) {
			Places.initialize(view.getContext(), getResources().getString(R.string.google_api_key));
		}
		placesClient = Places.createClient(view.getContext());
		autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_support_fragment);
		autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME));
		autocompleteSupportFragment.setHint(getString(R.string.destination));
		autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
	}

	@NonNull
	private PlaceSelectionListener getPlaceSelectionListener() {
		return new PlaceSelectionListener() {
			@Override
			public void onPlaceSelected(@NonNull Place place) {
				destinationName = place.getName();
				destinationLatLong = place.getLatLng();
			}

			@Override
			public void onError(@NonNull Status status) {
			}
		};
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		inflater.inflate(R.menu.map_options_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_options) {
			startActivity(new Intent(getActivity(), MapPreferences.class));
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferences();

		checkShowMyLocation();
		checkActiveDrivers();
	}

	private void checkShowMyLocation() {
		if (!showMyLocation) {
			stopLocationUpdates();
			geoFireProvider.deleteLocation(authProvider.getUserId());
		} else {
			if (mMap != null) {
				checkVersionToStartLocation();
			}
		}
	}

	private void checkActiveDrivers() {
		if (showOthers && currentLatLong != null) {
			getActiveDrivers();
		} else if (!showOthers) {
			for (Marker marker : driversMarkers) {
				if (marker.getTag() != null) {
					marker.remove();
					driversMarkers.remove(marker);
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopLocationUpdates();
	}

	private void stopLocationUpdates() {
		if (fusedLocationProviderClient != null) {
			fusedLocationProviderClient.removeLocationUpdates(locationCallback);
		}
	}

	public void getPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		showMyLocation = prefs.getBoolean(getString(R.string.prefShowMyLocation), false);
		showOthers = prefs.getBoolean(getString(R.string.prefShowOthers), false);
		distanceRadius = Long.parseLong(prefs.getString(getString(R.string.prefDistanceRadius), getString(R.string.defaultDistanceRadius)));
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
			checklAllForLocation();
		}
	}

	private void checklAllForLocation() {
		if (itsGpsActived()) {
			if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
				checklAllForLocation();
			} else {
				checkLocationPermissions();
			}
		} else {
			checklAllForLocation();
		}
	}

	private void checkLocationPermissions() {
		if (!PermissionUtils.hasPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
			if (PermissionUtils.shouldShowRational(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
				new AlertDialog.Builder(view.getContext()).setTitle(R.string.permissionLocationTitle).setMessage(view.getContext().getText(R.string.permissionLocation)).setPositiveButton("OK", (dialog, which) ->
						PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION)).create().show();
			} else {
				PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION);
			}
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == Constants.REQUEST_CODE_SETTINGS && itsGpsActived()) {
			if (ActivityCompat.checkSelfPermission(view.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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

	private void showAlertDialogNoGps() {
		AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

		builder.setMessage(R.string.pleaseActiveLocation).setPositiveButton("Settings", (dialog, which) ->
				startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.REQUEST_CODE_SETTINGS)).create().show();
	}

	private void updateLocation() {
		if (currentLatLong != null && showMyLocation) {
			geoFireProvider.saveLocation(authProvider.getUserId(), currentLatLong);
		}
	}

	private void getActiveDrivers() {
		if (showOthers) {
			geoFireProvider.getActiveDrivers(currentLatLong, distanceRadius).addGeoQueryEventListener(new GeoQueryEventListener() {
				@Override
				public void onKeyEntered(String key, GeoLocation location) {
					for (Marker marker : driversMarkers) {
						if (marker.getTag() != null && (marker.getTag().equals(key) || key.equals(authProvider.getUserId()))) {
							return;
						}
					}

					if (!key.equals(authProvider.getUserId())) {
						LatLng driverLatLong = new LatLng(location.latitude, location.longitude);
						Marker otherDriverMarker = mMap.addMarker(new MarkerOptions()
								.position(driverLatLong)
								.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto_other)));

						otherDriverMarker.setTag(key);
						driversMarkers.add(otherDriverMarker);
					}
				}

				@Override
				public void onKeyExited(String key) {
					for (Marker marker : driversMarkers) {
						if (marker.getTag() != null && marker.getTag().equals(key)) {
							marker.remove();
							driversMarkers.remove(marker);
							return;
						}
					}
				}

				@Override
				public void onKeyMoved(String key, GeoLocation location) {
					for (Marker marker : driversMarkers) {
						if (marker.getTag() != null && marker.getTag().equals(key) && !key.equals(authProvider.getUserId())) {
							marker.setPosition(new LatLng(location.latitude, location.longitude));
							return;
						}
					}
				}

				@Override
				public void onGeoQueryReady() {

				}

				@Override
				public void onGeoQueryError(DatabaseError error) {

				}
			});
		}
	}

}