package com.dcascos.motogo.layouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.signInSignUp.SignIn;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EmptyActivity extends AppCompatActivity {

	private Button btSignOut;

	private TextView tvFullName;
	private TextView tvEmail;

	private FirebaseAuth mAuth;
	private FirebaseFirestore mFirestore;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_empty);

		btSignOut = findViewById(R.id.bt_signOut);

		tvFullName = findViewById(R.id.tv_fullName);
		tvEmail = findViewById(R.id.tv_email);

		mAuth = FirebaseAuth.getInstance();
		mFirestore = FirebaseFirestore.getInstance();

		mFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
			@Override
			public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//				if (snapshot.exists()) {
//					String fullName = snapshot.child("fullName").getValue().toString();
//					String email = snapshot.child("email").getValue().toString();
//
//					tvFullName.setText(fullName);
//					tvEmail.setText(email);
//				}
			}
		});

		btSignOut.setOnClickListener(v -> {

			mAuth.signOut();

			startActivity(new Intent(EmptyActivity.this, SignIn.class));
			finish();

		});


	}
}