package com.dcascos.motogo.Layouts;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.Utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

public class Login extends AppCompatActivity {

	//Variables
	ImageView ivLogo;
	TextView tvWelcome, tvSignIn;
	TextInputLayout tiUsername, tiPassword;
	Button btGo, btSignUp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login);

		ivLogo = findViewById(R.id.iv_logo);
		tvWelcome = findViewById(R.id.tv_welcome);
		tvSignIn = findViewById(R.id.tv_signin);

		tiUsername = findViewById(R.id.ti_username);
		tiPassword = findViewById(R.id.ti_password);

		btGo = findViewById(R.id.bt_go);
		btSignUp = findViewById(R.id.bt_signUp);

		btGo.setOnClickListener(v -> {
			if (!Validations.validateUsernameFormat(getApplicationContext(), tiUsername)
					| !Validations.validatePasswordFormat(getApplicationContext(), tiPassword)) {
				return;
			} else {
				Intent intent = new Intent(Login.this, EmptyActivity.class);
				startActivity(intent);
				finish();
			}
		});

		btSignUp.setOnClickListener(v -> {
			Intent intent = new Intent(Login.this, SignUp.class);

			Pair[] pairs = new Pair[7];
			pairs[0] = new Pair<View, String>(ivLogo, "tran_logo");
			pairs[1] = new Pair<View, String>(tvWelcome, "tran_title");
			pairs[2] = new Pair<View, String>(tvSignIn, "tran_signInContinue");
			pairs[3] = new Pair<View, String>(tiUsername, "tran_username");
			pairs[4] = new Pair<View, String>(tiPassword, "tran_password");
			pairs[5] = new Pair<View, String>(btGo, "tran_go");
			pairs[6] = new Pair<View, String>(btSignUp, "tran_newUser");

			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);

			startActivity(intent, options.toBundle());
		});
	}
}