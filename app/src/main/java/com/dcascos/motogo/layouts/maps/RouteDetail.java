package com.dcascos.motogo.layouts.maps;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.models.Route;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.GoogleAPIProvider;
import com.dcascos.motogo.providers.RoutesProvider;
import com.dcascos.motogo.utils.DecodePoints;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RouteDetail extends AppCompatActivity implements OnMapReadyCallback {

	private TextView tvOrigin;
	private TextView tvDestination;
	private TextView tvDistance;
	private TextView tvDuration;
	private Button btStartRoute;
	private ImageButton ibBack;

	private GoogleMap mMap;
	private SupportMapFragment mapFragment;

	private String currentName;
	private String destinationName;
	private double currentLat;
	private double currentLon;
	private double destinationLat;
	private double destinationLon;
	private String distance;
	private String duration;
	private String points;

	private LatLng currentLatLong;
	private LatLng destinationLatLong;

	private GoogleAPIProvider googleAPIProvider;

	private List<LatLng> polylineList;
	private PolylineOptions polylineOptions;

	private AuthProvider authProvider;
	private RoutesProvider routesProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_route_detail);

		tvOrigin = findViewById(R.id.tv_origin);
		tvDestination = findViewById(R.id.tv_destination);
		tvDistance = findViewById(R.id.tv_distance);
		tvDuration = findViewById(R.id.tv_duration);
		btStartRoute = findViewById(R.id.bt_startRoute);
		ibBack = findViewById(R.id.ib_back);

		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		getIntentExtras();

		currentLatLong = new LatLng(currentLat, currentLon);
		destinationLatLong = new LatLng(destinationLat, destinationLon);

		googleAPIProvider = new GoogleAPIProvider(RouteDetail.this);

		authProvider = new AuthProvider();
		routesProvider = new RoutesProvider();

		btStartRoute.setOnClickListener(v -> createRoute());

		ibBack.setOnClickListener(v -> this.onBackPressed());
	}

	private void getIntentExtras() {
		currentName = getIntent().getStringExtra("currentName");
		destinationName = getIntent().getStringExtra("destinationName");
		currentLat = getIntent().getDoubleExtra("currentLat", 0);
		currentLon = getIntent().getDoubleExtra("currentLon", 0);
		destinationLat = getIntent().getDoubleExtra("destinationLat", 0);
		destinationLon = getIntent().getDoubleExtra("destinationLon", 0);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		mMap.addMarker(new MarkerOptions().position(currentLatLong).title("Origin").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto_me)));
		mMap.addMarker(new MarkerOptions().position(destinationLatLong).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)));

		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLatLong).zoom(14f).build()));

		drawRoute();
	}

	private void drawRoute() {
		googleAPIProvider.getDirections(currentLatLong, destinationLatLong).enqueue(new Callback<String>() {
			@Override
			public void onResponse(Call<String> call, Response<String> response) {
				try {
					JSONObject jsonObject = new JSONObject(response.body());

					JSONArray jsonArrayRoutes = jsonObject.getJSONArray("routes");
					JSONObject jsonObjectRoute = jsonArrayRoutes.getJSONObject(0);

					JSONObject polylines = jsonObjectRoute.getJSONObject("overview_polyline");
					points = polylines.getString("points");

					polylineList = DecodePoints.decodePoly(points);
					polylineOptions = new PolylineOptions();
					polylineOptions.color(Color.DKGRAY);
					polylineOptions.width(8f);
					polylineOptions.startCap(new SquareCap());
					polylineOptions.jointType(JointType.ROUND);
					polylineOptions.addAll(polylineList);

					mMap.addPolyline(polylineOptions);

					JSONArray jsonArrayLegs = jsonObjectRoute.getJSONArray("legs");
					JSONObject leg = jsonArrayLegs.getJSONObject(0);
					JSONObject jsonObjectDistance = leg.getJSONObject("distance");
					JSONObject jsonObjectDuration = leg.getJSONObject("duration");
					distance = jsonObjectDistance.getString("text");
					duration = jsonObjectDuration.getString("text");

					showInfo();

				} catch (Exception e) {
					Log.d("Error", e.getMessage());
				}
			}

			@Override
			public void onFailure(Call<String> call, Throwable t) {
			}
		});
	}

	private void showInfo() {
		tvOrigin.setText(currentName);
		tvDestination.setText(destinationName);
		tvDistance.setText(distance);
		tvDuration.setText(duration);

		btStartRoute.setVisibility(View.VISIBLE);
	}

	private void createRoute() {
		Route route = new Route();
		route.setUserId(authProvider.getUserId());
		route.setOrigin(currentName);
		route.setDestination(destinationName);
		route.setDistance(distance);
		route.setDuration(duration);
		route.setOriginLat(currentLat);
		route.setOriginLon(currentLon);
		route.setDestinationLat(destinationLat);
		route.setDestinationLon(destinationLon);
		route.setPoints(points);

		routesProvider.create(route).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				finish();
				Toast.makeText(RouteDetail.this, getText(R.string.postCreated), Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(RouteDetail.this, getText(R.string.postNoUploaded), Toast.LENGTH_SHORT).show();
			}
		});
	}
}
