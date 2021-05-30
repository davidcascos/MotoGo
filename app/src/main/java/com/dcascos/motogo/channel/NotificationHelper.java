package com.dcascos.motogo.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.dcascos.motogo.R;

public class NotificationHelper extends ContextWrapper {

	private static final String CHANNEL_ID = "com.dcascos.motogo";
	private static final String CHANNEL_NAME = "MotoGo";

	private NotificationManager notificationManager;

	public NotificationHelper(Context context) {
		super(context);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			createChannels();
		}
	}

	@RequiresApi(api = Build.VERSION_CODES.O)
	private void createChannels() {
		NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
		notificationChannel.enableLights(true);
		notificationChannel.enableVibration(true);
		notificationChannel.setLightColor(Color.GRAY);
		notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
		getManager().createNotificationChannel(notificationChannel);
	}

	public NotificationManager getManager() {
		if (notificationManager == null) {
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		}
		return notificationManager;
	}

	public NotificationCompat.Builder getNotification(String title, String body) {
		return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
				.setContentTitle(title)
				.setContentText(body)
				.setAutoCancel(true)
				.setColor(Color.GRAY)
				.setSmallIcon(R.mipmap.ic_motogo)
				.setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));
	}
}
