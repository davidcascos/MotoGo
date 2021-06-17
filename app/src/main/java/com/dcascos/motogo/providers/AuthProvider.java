package com.dcascos.motogo.providers;

import android.app.Activity;

import com.dcascos.motogo.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class AuthProvider {

	private final FirebaseAuth firebaseAuth;

	public AuthProvider() {
		firebaseAuth = FirebaseAuth.getInstance();
	}

	public Task<AuthResult> signIn(String email, String password) {
		return firebaseAuth.signInWithEmailAndPassword(email, password);
	}

	public Task<AuthResult> googleSignIn(GoogleSignInAccount googleSignInAccount) {
		AuthCredential credential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
		return firebaseAuth.signInWithCredential(credential);
	}

	public Task<AuthResult> signUp(String email, String password) {
		return firebaseAuth.createUserWithEmailAndPassword(email, password);
	}

	public Task<Void> resetPassword(String email) {
		firebaseAuth.setLanguageCode("es");
		return firebaseAuth.sendPasswordResetEmail(email);
	}

	public Boolean getUserLogged() {
		return firebaseAuth.getCurrentUser() != null;
	}

	public String getUserId() {
		if (getUserLogged()) {
			return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
		} else {
			return null;
		}
	}

	public String getUserEmail() {
		if (getUserLogged()) {
			return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail();
		} else {
			return null;
		}
	}

	public String getUserName() {
		if (getUserLogged()) {
			return Objects.requireNonNull(firebaseAuth.getCurrentUser()).getDisplayName();
		} else {
			return null;
		}
	}

	public void signOut(Activity activity) {
		if (firebaseAuth != null) {
			firebaseAuth.signOut();
			// Google sign out
			GoogleSignIn.getClient(activity, new GoogleSignInOptions
					.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestIdToken(activity.getString(R.string.default_web_client_id))
					.requestEmail().build()).signOut();
		}
	}

}
