package com.dcascos.motogo.Layouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.Utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

public class SignUp extends AppCompatActivity {

	TextInputLayout tiFullname, tiUsername, tiEmail, tiPassword;

	Button btGo, btBackLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_up);

		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btGo = findViewById(R.id.bt_go);
		btBackLogin = findViewById(R.id.bt_backLogin);

		btGo.setOnClickListener(v -> {
			if (!Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
					| !Validations.validateUsernameFormat(getApplicationContext(), tiUsername)
					| !Validations.validateEmailFormat(getApplicationContext(), tiEmail)
					| !Validations.validatePasswordFormat(getApplicationContext(), tiPassword)) {
				return;
			} else {
				Intent intent = new Intent(SignUp.this, EmptyActivity.class);
				startActivity(intent);
				finish();
			}
		});

		btBackLogin.setOnClickListener(v -> {
			this.onBackPressed();
		});
	}
}