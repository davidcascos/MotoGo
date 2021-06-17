package com.dcascos.motogo.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.UsersProvider;

import java.util.List;

public class isUserOnlineHelper {

	public static void updateOnline(boolean isOnline, final Context context) {
		UsersProvider usersProvider = new UsersProvider();
		AuthProvider authProvider = new AuthProvider();
		if (authProvider.getUserId() != null
				&& (isApplicationSentToBackground(context) || isOnline)) {
			usersProvider.updateOnline(authProvider.getUserId(), isOnline);
		}
	}

	public static boolean isApplicationSentToBackground(final Context context) {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
		if (!tasks.isEmpty()) {
			ComponentName topActivity = tasks.get(0).topActivity;
			return !topActivity.getPackageName().equals(context.getPackageName());
		}
		return false;
	}

}
