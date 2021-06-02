package com.dcascos.motogo.layouts.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import com.dcascos.motogo.utils.MainActivity;
import com.dcascos.motogo.R;

public class LoginPasswordResetSent extends MainActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login_password_reset_sent);

		ImageButton ibClose = findViewById(R.id.ib_close);

		ibClose.setOnClickListener(v -> {
			startActivity(new Intent(LoginPasswordResetSent.this, LoginSignIn.class));
			finish();
		});
	}
}