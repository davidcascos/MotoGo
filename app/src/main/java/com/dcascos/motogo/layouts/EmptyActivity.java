package com.dcascos.motogo.layouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.loginSignup.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmptyActivity extends AppCompatActivity {

	private Button btSignOut;

	private TextView tvFullName;
	private TextView tvEmail;

	private FirebaseAuth mAuth;
	private FirebaseDatabase database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_empty);

		btSignOut = findViewById(R.id.bt_signOut);

		tvFullName = findViewById(R.id.tv_fullName);
		tvEmail = findViewById(R.id.tv_email);

		mAuth = FirebaseAuth.getInstance();
		database = FirebaseDatabase.getInstance();
		DatabaseReference reference = database.getReference("Users");

		reference.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					String fullName = snapshot.child("fullName").getValue().toString();
					String email = snapshot.child("email").getValue().toString();

					tvFullName.setText(fullName);
					tvEmail.setText(email);
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {

			}
		});


		btSignOut.setOnClickListener(v -> {

			mAuth.signOut();

			startActivity(new Intent(EmptyActivity.this, Login.class));
			finish();

		});


	}
}