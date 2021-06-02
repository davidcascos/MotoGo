package com.dcascos.motogo.utils;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.dcascos.motogo.R;

public class NetworkChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		final PendingResult pendingResult = goAsync();
		Task asyncTask = new Task(pendingResult, intent, context);
		asyncTask.execute();
	}

	private class Task extends AsyncTask<String, String, Boolean> {

		private final PendingResult pendingResult;
		private final Intent intent;
		private final Context context;
		private Boolean networkConnected;
		private Boolean wifiConnected;

		private Task(PendingResult pendingResult, Intent intent, Context context) {
			this.pendingResult = pendingResult;
			this.intent = intent;
			this.context = context;
			this.networkConnected = Boolean.TRUE;
			this.wifiConnected = Boolean.TRUE;
		}

		@Override
		protected Boolean doInBackground(String... strings) {

			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			Network network = connectivityManager.getActiveNetwork();

			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			boolean onlyWifi = prefs.getBoolean(context.getString(R.string.prefNetwork), false);

			if (network != null) {
				NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
				if (networkCapabilities == null) {
					networkConnected = Boolean.FALSE;
					wifiConnected = Boolean.FALSE;
				} else if (onlyWifi
						&& !(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))) {
					networkConnected = Boolean.FALSE;
				}
			} else {
				networkConnected = Boolean.FALSE;
				wifiConnected = Boolean.FALSE;
			}

			return true;
		}

		@Override
		protected void onPostExecute(Boolean s) {
			super.onPostExecute(s);

			if (networkConnected == Boolean.FALSE && wifiConnected == Boolean.FALSE) {
				showDialog(context, intent);
				Toast.makeText(context, R.string.lost_connection, Toast.LENGTH_SHORT).show();
			} else if (networkConnected == Boolean.FALSE && wifiConnected == Boolean.TRUE) {
				showDialog(context, intent);
			}

			if (pendingResult != null) {
				pendingResult.finish();
			}
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