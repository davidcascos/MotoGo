package com.dcascos.motogo.Layouts;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.Utils.Validations;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Login extends AppCompatActivity {

	//Variables
	private ImageView ivLogo;
	private TextView tvWelcome;
	private TextView tvSignIn;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;
	private Button btGo;
	private Button btSignUp;

	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login);

		ivLogo = findViewById(R.id.iv_logo);
		tvWelcome = findViewById(R.id.tv_welcome);
		tvSignIn = findViewById(R.id.tv_signin);

		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btGo = findViewById(R.id.bt_go);
		btSignUp = findViewById(R.id.bt_signUp);

		mAuth = FirebaseAuth.getInstance();

		btSignUp.setOnClickListener(v -> {

			Pair[] pairs = new Pair[7];
			pairs[0] = new Pair<View, String>(ivLogo, "tran_logo");
			pairs[1] = new Pair<View, String>(tvWelcome, "tran_title");
			pairs[2] = new Pair<View, String>(tvSignIn, "tran_signInContinue");
			pairs[3] = new Pair<View, String>(tiEmail, "tran_email");
			pairs[4] = new Pair<View, String>(tiPassword, "tran_password");
			pairs[5] = new Pair<View, String>(btGo, "tran_go");
			pairs[6] = new Pair<View, String>(btSignUp, "tran_newUser");

			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);

			startActivity(new Intent(Login.this, SignUp.class), options.toBundle());
		});
	}

	public void doLogin(View view) {
		if (!Validations.validateIsEmpty(getApplicationContext(), tiEmail)
				| !Validations.validateIsEmpty(getApplicationContext(), tiPassword)) {
			return;
		} else {
			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					startActivity(new Intent(Login.this, EmptyActivity.class));
					finish();
				} else {
					Toast.makeText(Login.this, "No se puede entrar, revisa datos", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mAuth.getCurrentUser() != null) {
			startActivity(new Intent(Login.this, EmptyActivity.class));
			finish();
		}
	}
}