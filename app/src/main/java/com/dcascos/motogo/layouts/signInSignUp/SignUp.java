package com.dcascos.motogo.layouts.signInSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.EmptyActivity;
import com.dcascos.motogo.models.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

	private TextInputLayout tiFullname;
	private TextInputLayout tiUsername;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;

	private Button btBackSignIn;
	private Button btGo;

	private RelativeLayout progressBar;

	private AuthProvider mAuthProvider;
	private UsersProvider mUserProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_up);

		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btBackSignIn = findViewById(R.id.bt_backSignIn);
		btGo = findViewById(R.id.bt_go);

		progressBar = findViewById(R.id.rl_progress);

		mAuthProvider = new AuthProvider();
		mUserProvider = new UsersProvider();

		btBackSignIn.setOnClickListener(v -> this.onBackPressed());

		btGo.setOnClickListener(v -> doSignUp());
	}

	private void doSignUp() {
		if (Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
				& Validations.validateUsernameFormat(getApplicationContext(), tiUsername)
				& Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				& Validations.validatePasswordFormat(getApplicationContext(), tiPassword)) {

			progressBar.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			String fullname = Objects.requireNonNull(tiFullname.getEditText()).getText().toString().trim();
			String username = Objects.requireNonNull(tiUsername.getEditText()).getText().toString().trim();
			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			mAuthProvider.signUp(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					String userId = mAuthProvider.getUserId();
					User user = new User(userId, fullname, username, email);

					mUserProvider.createUser(user).addOnCompleteListener(task1 -> {
						if (task1.isSuccessful()) {
							startActivity(new Intent(SignUp.this, EmptyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
						} else {
							progressBar.setVisibility(View.GONE);
							getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
							Toast.makeText(SignUp.this, getText(R.string.userCouldNotBeCreated), Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					progressBar.setVisibility(View.GONE);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					Toast.makeText(SignUp.this, getText(R.string.alreadyUserWithMail), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}