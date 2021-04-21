package com.dcascos.motogo.layouts.signInSignUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

	private RelativeLayout progressBar;

	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_reset_password);

		ibBack = findViewById(R.id.ib_back);
		tiEmail = findViewById(R.id.ti_email);

		progressBar = findViewById(R.id.rl_progress);

		mAuth = FirebaseAuth.getInstance();

		ibBack.setOnClickListener(v -> this.onBackPressed());
	}

	public void resetPassword(View view) {

		if (Validations.validateEmailFormat(getApplicationContext(), tiEmail)) {

			progressBar.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();

			mAuth.setLanguageCode("es");
			mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					startActivity(new Intent(ResetPassword.this, ResetPasswordSent.class));
					finish();
				} else {
					progressBar.setVisibility(View.GONE);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					Toast.makeText(ResetPassword.this, getText(R.string.noUserWithThisMail), Toast.LENGTH_SHORT).show();
				}
			});
		}
	}
}