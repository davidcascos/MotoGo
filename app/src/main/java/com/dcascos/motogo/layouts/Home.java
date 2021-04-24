package com.dcascos.motogo.layouts;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.dcascos.motogo.R;
import com.dcascos.motogo.fragments.HomeFragment;
import com.dcascos.motogo.fragments.ProfileFragment;
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
	}

	public void openFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.container, fragment);
		transaction.commit();
	}

	BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
		switch (item.getItemId()) {
			case R.id.itemHome:
				openFragment(new HomeFragment());
				return true;
			case R.id.itemItem2:
				openFragment(new HomeFragment());
				return true;
			case R.id.itemItem3:
				openFragment(new HomeFragment());
				return true;
			case R.id.itemProfile:
				openFragment(new ProfileFragment());
				return true;
			default:
				return false;
		}
	};
}