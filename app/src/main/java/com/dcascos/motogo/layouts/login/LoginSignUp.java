package com.dcascos.motogo.layouts.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.home.Home;
import com.dcascos.motogo.models.database.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Date;
import java.util.Objects;

public class LoginSignUp extends AppCompatActivity {

	private RelativeLayout progressBar;
	private TextInputLayout tiFullname;
	private TextInputLayout tiUsername;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;
	private Button btBackSignIn;
	private Button btGo;

	private AuthProvider authProvider;
	private ImageProvider imageProvider;
	private UsersProvider usersProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login_signup);

		progressBar = findViewById(R.id.rl_progress);
		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);
		btBackSignIn = findViewById(R.id.bt_backSignIn);
		btGo = findViewById(R.id.bt_go);

		authProvider = new AuthProvider();
		imageProvider = new ImageProvider();
		usersProvider = new UsersProvider();

		btBackSignIn.setOnClickListener(v -> this.onBackPressed());
		btGo.setOnClickListener(v -> doSignUp());
	}

	private void doSignUp() {
		if (Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
				& Validations.validateUsernameFormat(getApplicationContext(), tiUsername)
				& Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				& Validations.validatePasswordFormat(getApplicationContext(), tiPassword)) {

			showProgressBar();

			String fullname = Objects.requireNonNull(tiFullname.getEditText()).getText().toString().trim();
			String username = Objects.requireNonNull(tiUsername.getEditText()).getText().toString().trim();
			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			usersProvider.checkUsernameExists(username).get().addOnSuccessListener(queryDocumentSnapshots -> {
				if (queryDocumentSnapshots.isEmpty()) {
					authProvider.signUp(email, password).addOnCompleteListener(task -> {
						if (task.isSuccessful()) {
							imageProvider.saveCoverWithoutImage().getDownloadUrl().addOnSuccessListener(uriCover ->
									imageProvider.saveProfileWithoutImage().getDownloadUrl().addOnSuccessListener(uriProfile -> {
										String userId = authProvider.getUserId();
										User user = new User(userId, fullname, username, email, uriCover.toString(), uriProfile.toString(), new Date().getTime(), new Date().getTime());

										usersProvider.createUser(user).addOnCompleteListener(task1 -> {
											if (task1.isSuccessful()) {
												startActivity(new Intent(LoginSignUp.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
											} else {
												hideProgressBar();
												Toast.makeText(LoginSignUp.this, getText(R.string.userCouldNotBeCreated), Toast.LENGTH_SHORT).show();
											}
										});
									}));
						} else {
							hideProgressBar();
							Toast.makeText(LoginSignUp.this, getText(R.string.alreadyUserWithMail), Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					hideProgressBar();
					Toast.makeText(LoginSignUp.this, getText(R.string.alreadyUserWithUsername), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	private void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}

	private void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}
}