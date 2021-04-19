package com.dcascos.motogo.Layouts;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.google.firebase.auth.FirebaseAuth;

public class EmptyActivity extends AppCompatActivity {

	private Button btSignOut;
	private FirebaseAuth mAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_empty);

		btSignOut = findViewById(R.id.bt_signOut);
		mAuth = FirebaseAuth.getInstance();

		btSignOut.setOnClickListener(v -> {

			mAuth.signOut();

			startActivity(new Intent(EmptyActivity.this, Login.class));
			finish();

		});


	}
}