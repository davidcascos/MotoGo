package com.dcascos.motogo.layouts.maps;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
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
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.profile.ProfileFromUser;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.GeoFireProvider;
import com.dcascos.motogo.providers.database.RoutesProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
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
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

	private View view;
	private CardView cvAutocomplete;
	private boolean showMyLocation;
	private boolean showOtherDrivers;
	private double distanceRadiusOtherDrivers;
	private boolean showRoutes;
	private double distanceRadiusRoutes;
	private GoogleMap mMap;
	private LocationRequest locationRequest;
	private FusedLocationProviderClient fusedLocationProviderClient;
	private LatLng originLatLong;
	private LatLng destinationLatLong;

	private AuthProvider authProvider;
	private GeoFireProvider geoFireProvider;
	private RoutesProvider routesProvider;
	private UsersProvider usersProvider;

	private Marker meMarker;
	private List<Marker> driversMarkers = new ArrayList<>();
	private List<Marker> routesMarkers = new ArrayList<>();
	private String originName;
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

					originLatLong = new LatLng(location.getLatitude(), location.getLongitude());
					checkActiveDrivers();
					checkRoutes();

					mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(new LatLng(location.getLatitude(), location.getLongitude())).zoom(15f).build()));

					updateLocation();

					cvAutocomplete.setVisibility(View.VISIBLE);
				}
			}
		}

		private void updateLocation() {
			if (originLatLong != null) {
				getOriginInfo();

				if (showMyLocation) {
					geoFireProvider.saveLocation(authProvider.getUserId(), originLatLong);
				}
			}
		}

		private void getOriginInfo() {
			Geocoder geocoder = new Geocoder(getContext());
			try {
				List<Address> addressList = geocoder.getFromLocation(originLatLong.latitude, originLatLong.longitude, 1);
				originName = addressList.get(0).getAddressLine(0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_maps, container, false);
		getPreferences();

		cvAutocomplete = view.findViewById(R.id.cv_autocomplete);

		Toolbar toolbar = view.findViewById(R.id.toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.empty));
		setHasOptionsMenu(true);

		SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		authProvider = new AuthProvider();
		geoFireProvider = new GeoFireProvider();
		routesProvider = new RoutesProvider();
		usersProvider = new UsersProvider();

		fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(view.getContext());

		autocompleteDestination();

		btCreateRoute = view.findViewById(R.id.bt_createRoute);
		btCreateRoute.setOnClickListener(v -> createRoute());

		return view;
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

	public void getPreferences() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		showMyLocation = prefs.getBoolean(getString(R.string.prefShowMyLocation), false);
		showOtherDrivers = prefs.getBoolean(getString(R.string.prefShowOthers), false);
		distanceRadiusOtherDrivers = Long.parseLong(prefs.getString(getString(R.string.prefDistanceRadiusOtherDrivers), getString(R.string.defaultDistanceRadius)));
		showRoutes = prefs.getBoolean(getString(R.string.prefShowRoutes), false);
		distanceRadiusRoutes = Long.parseLong(prefs.getString(getString(R.string.prefDistanceRadiusRoutes), getString(R.string.defaultDistanceRadius)));
	}

	private void autocompleteDestination() {
		if (!Places.isInitialized()) {
			Places.initialize(view.getContext(), getResources().getString(R.string.google_api_key));
		}
		AutocompleteSupportFragment autocompleteSupportFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_support_fragment);
		autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));
		autocompleteSupportFragment.setHint(getString(R.string.destination));
		autocompleteSupportFragment.setOnPlaceSelectedListener(getPlaceSelectionListener());
	}

	@NonNull
	private PlaceSelectionListener getPlaceSelectionListener() {
		return new PlaceSelectionListener() {
			@Override
			public void onPlaceSelected(@NonNull Place place) {
				destinationName = place.getAddress();
				destinationLatLong = place.getLatLng();
				btCreateRoute.setVisibility(View.VISIBLE);
			}

			@Override
			public void onError(@NonNull Status status) {
			}
		};
	}

	private void createRoute() {
		if (originLatLong != null && destinationLatLong != null) {
			Intent intent = new Intent(getContext(), MapsRouteDetail.class);
			intent.putExtra("from", "create");
			intent.putExtra("originName", originName);
			intent.putExtra("destinationName", destinationName);
			intent.putExtra("originLat", originLatLong.latitude);
			intent.putExtra("originLon", originLatLong.longitude);
			intent.putExtra("destinationLat", destinationLatLong.latitude);
			intent.putExtra("destinationLon", destinationLatLong.longitude);

			startActivity(intent);
		} else {
			Toast.makeText(getContext(), R.string.selectDestination, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onMapReady(@NonNull GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled(true);
		mMap.setOnInfoWindowClickListener(this);

		locationRequest = LocationRequest.create()
				.setInterval(1000)
				.setFastestInterval(1000)
				.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
				.setSmallestDisplacement(5);

		checkVersionToStartLocation();
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		if (driversMarkers.contains(marker)) {
			startActivity(new Intent(getContext(), ProfileFromUser.class).putExtra("userToViewId", marker.getTag().toString()));
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
				new AlertDialog.Builder(view.getContext())
						.setTitle(R.string.permissionLocationTitle)
						.setMessage(view.getContext().getText(R.string.permissionLocation))
						.setPositiveButton("OK", (dialog, which) ->
								PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION))
						.create().show();
			} else {
				PermissionUtils.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION);
			}
		}
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

	private void showAlertDialogNoGps() {
		AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

		builder.setMessage(R.string.pleaseActiveLocation)
				.setPositiveButton("Settings", (dialog, which) ->
						startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), Constants.REQUEST_CODE_SETTINGS))
				.create().show();
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

	private void checkActiveDrivers() {
		for (Marker marker : driversMarkers) {
			if (marker.getTag() != null) {
				marker.remove();
			}
		}
		driversMarkers = new ArrayList<>();

		if (showOtherDrivers && originLatLong != null) {
			getActiveDrivers();
		}
	}

	private void getActiveDrivers() {
		if (showOtherDrivers) {
			geoFireProvider.getActiveDrivers(originLatLong, distanceRadiusOtherDrivers).addGeoQueryEventListener(new GeoQueryEventListener() {
				@Override
				public void onKeyEntered(String key, GeoLocation location) {
					for (Marker marker : driversMarkers) {
						if (marker.getTag() != null && (marker.getTag().equals(key) || key.equals(authProvider.getUserId()))) {
							return;
						}
					}

					if (!key.equals(authProvider.getUserId())) {
						usersProvider.getUser(key).addOnSuccessListener(documentSnapshot -> {
							String username = "";
							if (documentSnapshot.exists() && documentSnapshot.contains(Constants.USER_USERNAME)) {
								username = documentSnapshot.getString(Constants.USER_USERNAME);
							}
							LatLng driverLatLong = new LatLng(location.latitude, location.longitude);
							Marker otherDriverMarker = mMap.addMarker(new MarkerOptions()
									.position(driverLatLong)
									.title(username)
									.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto_other)));

							otherDriverMarker.setTag(key);
							driversMarkers.add(otherDriverMarker);
						});
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

	private void checkRoutes() {
		for (Marker marker : routesMarkers) {
			if (marker.getTag() != null) {
				marker.remove();
			}
		}
		routesMarkers = new ArrayList<>();

		if (showRoutes && originLatLong != null) {
			getRoutes();
		}
	}

	private void getRoutes() {
		if (showRoutes) {
			routesProvider.getAll().addOnCompleteListener(task -> {
				if (task.isSuccessful() && !task.getResult().getDocuments().isEmpty()) {
					for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
						String key;
						if (documentSnapshot.contains(Constants.ROUTE_ID)) {
							key = documentSnapshot.getString(Constants.ROUTE_ID);
						} else {
							return;
						}

						for (Marker marker : routesMarkers) {
							if (marker.getTag() != null && (marker.getTag().equals(key))) {
								return;
							}
						}

						if (documentSnapshot.contains(Constants.ROUTE_ORIGINLAT) && documentSnapshot.contains(Constants.ROUTE_ORIGINLON)) {
							LatLng routeLatLng = new LatLng(documentSnapshot.getDouble(Constants.ROUTE_ORIGINLAT), documentSnapshot.getDouble(Constants.ROUTE_ORIGINLON));

							if (SphericalUtil.computeDistanceBetween(originLatLong, routeLatLng) <= distanceRadiusRoutes * 1000) {
								Marker routeMarker = mMap.addMarker(new MarkerOptions()
										.position(routeLatLng)
										.title("FROM: " + documentSnapshot.getString(Constants.ROUTE_ORIGIN))
										.snippet("TO: " + documentSnapshot.getString(Constants.ROUTE_DESTINATION))
										.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_route)));
								routeMarker.setTag(key);
								routesMarkers.add(routeMarker);
							}
						}
					}
				}
			});
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferences();
		checkActiveDrivers();
		checkRoutes();
		checkShowMyLocation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopLocationUpdates();
	}

	private void checkShowMyLocation() {
		if (!showMyLocation) {
			geoFireProvider.deleteLocation(authProvider.getUserId());
		} else {
			if (mMap != null) {
				checkVersionToStartLocation();
			}
		}
	}

	private void stopLocationUpdates() {
		if (fusedLocationProviderClient != null) {
			fusedLocationProviderClient.removeLocationUpdates(locationCallback);
		}
	}

}