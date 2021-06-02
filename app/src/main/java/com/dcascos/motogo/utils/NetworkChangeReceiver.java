package com.dcascos.motogo.utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dcascos.motogo.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

	public static final String WIFI = "Wi-Fi";
	public static final String ANY = "Any";

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			Network network = connectivityManager.getActiveNetwork();

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String actualNetwork = prefs.getString(context.getString(R.string.prefNetwork), context.getString(R.string.defaultNetwork));

			if (network != null) {
				NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
				if (networkCapabilities == null) {
					showDialog(context, intent);
					Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
				} else if (WIFI.equals(actualNetwork)
						&& !(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))) {
					showDialog(context, intent);
				} else if(WIFI.equals(actualNetwork)
						&& (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))){
					Toast.makeText(context, R.string.wifi_connected, Toast.LENGTH_SHORT).show();
				}
			} else {
				showDialog(context, intent);
			}

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	public void showDialog(Context context, Intent intent) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		View layoutDialog = LayoutInflater.from(context).inflate(R.layout.dialog_no_network, null);
		builder.setView(layoutDialog);

		Button btRetry = layoutDialog.findViewById(R.id.bt_retry);

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
		alertDialog.setCancelable(false);
		alertDialog.getWindow().setGravity(Gravity.CENTER);

		btRetry.setOnClickListener(v -> {
			alertDialog.dismiss();
			onReceive(context, intent);
		});
	}
}