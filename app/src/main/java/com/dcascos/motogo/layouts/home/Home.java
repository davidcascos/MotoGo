package com.dcascos.motogo.layouts.home;

import android.Manifest;
import android.app.AlertDialog;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.maps.MapsFragment;
import com.dcascos.motogo.layouts.profile.ProfileFragment;
import com.dcascos.motogo.utils.PermissionUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {

	private BottomNavigationView bottomNavigation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_home);

		bottomNavigation = findViewById(R.id.bottom_navigation);

		bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
		openFragment(new HomeFragment());

		checkLocationPermissions();
	}

	private void openFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, fragment);
		transaction.commit();
	}

	final BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
		switch (item.getItemId()) {
			case R.id.itemHome:
				openFragment(new HomeFragment());
				return true;
			case R.id.itemMap:
				openFragment(new MapsFragment());
				return true;
//			case R.id.itemItem3:
//				openFragment(new HomeFragment());
//				return true;
			case R.id.itemProfile:
				openFragment(new ProfileFragment());
				return true;
			default:
				return false;
		}
	};


	private void checkLocationPermissions() {
		if (!PermissionUtils.hasPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
			if (PermissionUtils.shouldShowRational(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
				new AlertDialog.Builder(this).setTitle(R.string.permissionLocationTitle).setMessage(getText(R.string.permissionLocation)).setPositiveButton("OK", (dialog, which) ->
						PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION)).create().show();
			} else {
				PermissionUtils.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_LOCATION);
			}
		}
	}
}