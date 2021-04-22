package com.dcascos.motogo.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {

	private FirebaseAuth mAuth;

	public AuthProvider() {
		mAuth = FirebaseAuth.getInstance();
	}

	public Task<AuthResult> signIn(String email, String password) {
		return mAuth.signInWithEmailAndPassword(email, password);
	}

	public Task<AuthResult> googleSignIn(GoogleSignInAccount googleSignInAccount) {
		AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
		return mAuth.signInWithCredential(credential);
	}

	public Task<AuthResult> signUp(String email, String password) {
		return mAuth.createUserWithEmailAndPassword(email, password);
	}

	public Task<Void> resetPassword(String email) {
		mAuth.setLanguageCode("es");
		return mAuth.sendPasswordResetEmail(email);
	}

	public Boolean getUserLogged() {
		return mAuth.getCurrentUser() != null;
	}

	public String getUserId() {
		if (getUserLogged()) {
			return mAuth.getCurrentUser().getUid();
		} else {
			return null;
		}
	}

	public String getUserEmail() {
		if (getUserLogged()) {
			return mAuth.getCurrentUser().getEmail();
		} else {
			return null;
		}
	}

	public String getUserName() {
		if (getUserLogged()) {
			return mAuth.getCurrentUser().getDisplayName();
		} else {
			return null;
		}
	}

}
