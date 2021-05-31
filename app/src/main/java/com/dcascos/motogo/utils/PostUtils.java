package com.dcascos.motogo.utils;

import android.content.Context;
import android.util.Log;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.FCMBody;
import com.dcascos.motogo.models.FCMResponse;
import com.dcascos.motogo.providers.GoogleAPIProvider;
import com.dcascos.motogo.providers.TokenProvider;
import com.dcascos.motogo.providers.database.UsersProvider;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostUtils {

	private static TokenProvider tokenProvider;
	private static UsersProvider usersProvider;
	private static GoogleAPIProvider googleAPIProvider;

	public static void sendNotification(Context context, String userId, String postUserId, String notificationType) {
		usersProvider = new UsersProvider();
		tokenProvider = new TokenProvider();
		googleAPIProvider = new GoogleAPIProvider();

		if (userId.equals(postUserId)) {
			return;
		}

		tokenProvider.getToken(postUserId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists() && documentSnapshot.contains(Constants.TOKENS_TOKEN)) {
				usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot1 -> {
					if (documentSnapshot1.exists() && (documentSnapshot1.contains(Constants.USER_USERNAME))) {
						String username = documentSnapshot1.getString(Constants.USER_USERNAME);

						String token = documentSnapshot.getString(Constants.TOKENS_TOKEN);
						Map<String, String> data = new HashMap<>();

						switch (notificationType) {
							case Constants.LIKES:
								data.put("title", context.getString(R.string.notificationLikes, username));
								break;
							case Constants.COMMENTS:
								data.put("title", context.getString(R.string.notificationComments, username));
								break;
						}

						FCMBody body = new FCMBody(token, "high", "4500s", data);

						googleAPIProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
							@Override
							public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
								if (response.body() != null) {
									if (response.body().getSuccess() != 1) {
										Log.d("Error", "response.body().getsucces() != 1");
									}
								} else {
									Log.d("Error", "Response body is null");
								}
							}

							@Override
							public void onFailure(Call<FCMResponse> call, Throwable t) {

							}
						});
					}
				});
			}
		});
	}

}
