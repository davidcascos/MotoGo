package com.dcascos.motogo.layouts.signInSignUp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.EmptyActivity;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

	private ImageView ivLogo;

	private TextView tvWelcome;
	private TextView tvSignIn;

	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;

	private Button btGo;
	private Button btSignUp;
	private Button btForgetPassword;

	private RelativeLayout progressBar;

	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_in);

		ivLogo = findViewById(R.id.iv_logo);
		tvWelcome = findViewById(R.id.tv_welcome);
		tvSignIn = findViewById(R.id.tv_signin);

		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btGo = findViewById(R.id.bt_go);
		btSignUp = findViewById(R.id.bt_signUp);
		btForgetPassword = findViewById(R.id.bt_forgetPassword);

		progressBar = findViewById(R.id.rl_progress);

		mAuth = FirebaseAuth.getInstance();

		btForgetPassword.setOnClickListener(v -> startActivity(new Intent(SignIn.this, ResetPassword.class)));

		btSignUp.setOnClickListener(v -> {

			Pair[] pairs = new Pair[7];
			pairs[0] = new Pair<View, String>(ivLogo, "tran_logo");
			pairs[1] = new Pair<View, String>(tvWelcome, "tran_title");
			pairs[2] = new Pair<View, String>(tvSignIn, "tran_signInContinue");
			pairs[3] = new Pair<View, String>(tiEmail, "tran_email");
			pairs[4] = new Pair<View, String>(tiPassword, "tran_password");
			pairs[5] = new Pair<View, String>(btGo, "tran_go");
			pairs[6] = new Pair<View, String>(btSignUp, "tran_newUser");

			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs);

			startActivity(new Intent(SignIn.this, SignUp.class), options.toBundle());
		});
	}

	public void doSignIn(View view) {
		if (Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				& Validations.validateIsEmpty(getApplicationContext(), tiPassword)) {

			progressBar.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					startActivity(new Intent(SignIn.this, EmptyActivity.class));
					finish();
				} else {
					progressBar.setVisibility(View.GONE);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					Toast.makeText(SignIn.this, getText(R.string.incorrectUsernameOrPassword), Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mAuth.getCurrentUser() != null) {
			startActivity(new Intent(SignIn.this, EmptyActivity.class));
			finish();
		}
	}
}