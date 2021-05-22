package com.dcascos.motogo.layouts.maps;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class RouteDetail extends AppCompatActivity implements OnMapReadyCallback {

	private TextView tvOrigin;
	private TextView tvDestination;
	private Button btStartRoute;
	private ImageButton ibBack;

	private GoogleMap mMap;
	private SupportMapFragment mapFragment;

	private double currentLat;
	private double currentLon;
	private double destinationLat;
	private double destinationLon;

	private LatLng currentLatLong;
	private LatLng destinationLatLong;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_route_detail);

		tvOrigin = findViewById(R.id.tv_origin);
		tvDestination = findViewById(R.id.tv_destination);
		btStartRoute = findViewById(R.id.bt_startRoute);
		ibBack = findViewById(R.id.ib_back);

		mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

		currentLat = getIntent().getDoubleExtra("currentLat", 0);
		currentLon = getIntent().getDoubleExtra("currentLon", 0);
		destinationLat = getIntent().getDoubleExtra("destinationLat", 0);
		destinationLon = getIntent().getDoubleExtra("destinationLon", 0);

		currentLatLong = new LatLng(currentLat, currentLon);
		destinationLatLong = new LatLng(destinationLat, destinationLon);

		ibBack.setOnClickListener(v -> this.onBackPressed());


	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		mMap.getUiSettings().setZoomControlsEnabled(true);

		mMap.addMarker(new MarkerOptions().position(currentLatLong).title("Origin").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_moto_me)));
		mMap.addMarker(new MarkerOptions().position(destinationLatLong).title("Destination").icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_destination)));

		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().target(currentLatLong).zoom(14f).build()));
	}
}
