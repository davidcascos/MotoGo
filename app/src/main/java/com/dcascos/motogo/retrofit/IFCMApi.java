package com.dcascos.motogo.retrofit;

import com.dcascos.motogo.models.FCMBody;
import com.dcascos.motogo.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

	@Headers({
			"Content-Type:application/json",
			"Authorization:key=AAAAVSYeaSU:APA91bHvZaskvZeN5V2j_I7KK2E2uLs2_vnGjRVBnx36I1UCGjnciRd0AHdqxjherTpXP4SkJhbuwA5EtE0aME6Vz_ZcYRHf5QR6xUEdIe1nTGcmmOM4XhZ0pSBzGrofwoYZtj1l9j5W"
	})
	@POST("fcm/send")
	Call<FCMResponse> send(@Body FCMBody body);
}