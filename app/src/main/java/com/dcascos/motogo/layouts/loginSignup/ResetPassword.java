package com.dcascos.motogo.layouts.loginSignup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class ResetPassword extends AppCompatActivity {


	private ImageButton ibBack;
	private TextInputLayout tiEmail;

	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_reset_password);

		ibBack = findViewById(R.id.ib_back);
		tiEmail = findViewById(R.id.ti_email);

		mAuth = FirebaseAuth.getInstance();

		ibBack.setOnClickListener(v -> this.onBackPressed());
	}

	public void resetPassword(View view) {

		if (Validations.validateEmailFormat(getApplicationContext(), tiEmail)) {
			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();

			mAuth.setLanguageCode("es");
			mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					startActivity(new Intent(ResetPassword.this, ResetPasswordSent.class));
					finish();
				} else {
					Toast.makeText(ResetPassword.this, getText(R.string.noUserWithThisMail), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}