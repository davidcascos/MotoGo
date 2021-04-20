package com.dcascos.motogo.layouts.loginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;

public class ResetPasswordSent extends AppCompatActivity {

	private ImageButton ibClose;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_reset_password_sent);

		ibClose = findViewById(R.id.ib_close);

		ibClose.setOnClickListener(v -> {
			startActivity(new Intent(ResetPasswordSent.this, Login.class));
			finish();
		});
	}
}