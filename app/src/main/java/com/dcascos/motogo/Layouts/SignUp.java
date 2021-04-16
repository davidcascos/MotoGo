package com.dcascos.motogo.Layouts;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {

	TextInputLayout ti_fullname, tiUsername, ti_email, tiPassword;

	Button btGo, bt_backLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_up);

		ti_fullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		ti_email = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btGo = findViewById(R.id.bt_go);
		bt_backLogin = findViewById(R.id.bt_backLogin);

		bt_backLogin.setOnClickListener(v -> {
			this.onBackPressed();
		});
	}
}