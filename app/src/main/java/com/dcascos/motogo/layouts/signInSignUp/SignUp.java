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
import com.dcascos.motogo.database.UserHelper;
import com.dcascos.motogo.layouts.EmptyActivity;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

	private TextInputLayout tiFullname;
	private TextInputLayout tiUsername;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;

	private Button btBackSignIn;

	private RelativeLayout progressBar;

	private FirebaseAuth mAuth;
	private FirebaseFirestore mFirestore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_up);

		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btBackSignIn = findViewById(R.id.bt_backSignIn);

		progressBar = findViewById(R.id.rl_progress);

		mAuth = FirebaseAuth.getInstance();
		mFirestore = FirebaseFirestore.getInstance();

		btBackSignIn.setOnClickListener(v -> this.onBackPressed());
	}

	public void doSignUp(View view) {

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

			mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					String idUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
					UserHelper userHelper = new UserHelper(fullname, username, email);

					mFirestore.collection("Users").document(idUser).set(userHelper).addOnCompleteListener(task1 -> {
						if (task1.isSuccessful()) {
							startActivity(new Intent(SignUp.this, EmptyActivity.class));
							finish();
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