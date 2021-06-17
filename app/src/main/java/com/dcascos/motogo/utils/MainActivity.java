package com.dcascos.motogo.utils;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;

public class MainActivity extends AppCompatActivity {

	BroadcastReceiver broadcastReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login_signin);

		broadcastReceiver = new NetworkChangeReceiver();
	}

	@Override
	protected void onStart() {
		super.onStart();
		registerReceiver(broadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

		isUserOnlineHelper.updateOnline(true, MainActivity.this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		isUserOnlineHelper.updateOnline(false, MainActivity.this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			unregisterReceiver(broadcastReceiver);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

}
