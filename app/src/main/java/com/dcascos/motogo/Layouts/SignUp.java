package com.dcascos.motogo.Layouts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.Database.UserHelper;
import com.dcascos.motogo.R;
import com.dcascos.motogo.Utils.Validations;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUp extends AppCompatActivity {

	private TextInputLayout tiFullname;
	private TextInputLayout tiUsername;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;

	private Button btBackLogin;

	private FirebaseAuth mAuth;
	private FirebaseDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_up);

		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btBackLogin = findViewById(R.id.bt_backLogin);

		mAuth = FirebaseAuth.getInstance();
		database = FirebaseDatabase.getInstance();

		btBackLogin.setOnClickListener(v -> {
			this.onBackPressed();
		});
	}

	public void doSignUp(View view) {

		if (!Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
				| !Validations.validateUsernameFormat(getApplicationContext(), tiUsername)
				| !Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				| !Validations.validatePasswordFormat(getApplicationContext(), tiPassword)) {
			return;
		} else {
			String fullname = Objects.requireNonNull(tiFullname.getEditText()).getText().toString().trim();
			String username = Objects.requireNonNull(tiUsername.getEditText()).getText().toString().trim();
			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {

					DatabaseReference reference = database.getReference("Users");

					String idUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
					UserHelper userHelper = new UserHelper(fullname, username, email);

					reference.child(idUser).setValue(userHelper).addOnCompleteListener(task2 -> {
						if (task2.isSuccessful()) {
							startActivity(new Intent(SignUp.this, EmptyActivity.class));
							finish();
						} else {
							Toast.makeText(SignUp.this, "Todo mas mal", Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					Toast.makeText(SignUp.this, "Todo mal", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}