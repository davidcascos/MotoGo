package com.dcascos.motogo.layouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.signInSignUp.SignIn;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class EmptyActivity extends AppCompatActivity {

	private Button btSignOut;

	private TextView tvFullName;
	private TextView tvEmail;

	private FirebaseAuth mAuth;
	private GoogleSignInClient mGoogleSignInClient;

	private FirebaseFirestore mFirestore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_empty);

		btSignOut = findViewById(R.id.bt_signOut);

		tvFullName = findViewById(R.id.tv_fullName);
		tvEmail = findViewById(R.id.tv_email);

		mAuth = FirebaseAuth.getInstance();
		// Configure Google Sign In
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		mFirestore = FirebaseFirestore.getInstance();

		String idUser = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

//				if (snapshot.exists()) {
//					String fullName = snapshot.child("fullName").getValue().toString();
//					String email = snapshot.child("email").getValue().toString();
//
//					tvFullName.setText(fullName);
//					tvEmail.setText(email);
//				}


		btSignOut.setOnClickListener(v -> {
			// Firebase sign out
			mAuth.signOut();

			// Google sign out
			mGoogleSignInClient.signOut().addOnCompleteListener(this, task -> {
			});

			startActivity(new Intent(EmptyActivity.this, SignIn.class));
			finish();
		});

	}
}