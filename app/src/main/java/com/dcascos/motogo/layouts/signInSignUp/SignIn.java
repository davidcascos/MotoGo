package com.dcascos.motogo.layouts.signInSignUp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.Home;
import com.dcascos.motogo.models.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.Generators;
import com.dcascos.motogo.utils.Validations;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

public class SignIn extends AppCompatActivity {

	private ImageView ivLogo;
	private TextView tvWelcome;
	private TextView tvSignIn;

	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;

	private Button btGo;
	private SignInButton btGoogle;
	private Button btSignUp;
	private Button btForgetPassword;

	private RelativeLayout progressBar;

	private AuthProvider mAuthProvider;
	private UsersProvider mUserProvider;
	private GoogleSignInClient mGoogleSignInClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_sign_in);

		ivLogo = findViewById(R.id.iv_logo);
		tvWelcome = findViewById(R.id.tv_welcome);
		tvSignIn = findViewById(R.id.tv_signin);

		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		btGo = findViewById(R.id.bt_go);
		btGoogle = findViewById(R.id.bt_google);
		btSignUp = findViewById(R.id.bt_signUp);
		btForgetPassword = findViewById(R.id.bt_forgetPassword);

		progressBar = findViewById(R.id.rl_progress);

		mAuthProvider = new AuthProvider();
		mUserProvider = new UsersProvider();

		// Configure Google Sign In
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
		mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

		btGo.setOnClickListener(v -> doSignIn());
		btGoogle.setOnClickListener(v -> signInGoogle());

		btForgetPassword.setOnClickListener(v -> startActivity(new Intent(SignIn.this, ResetPassword.class)));

		btSignUp.setOnClickListener(v -> {
			Pair[] pairs = new Pair[7];
			pairs[0] = new Pair<View, String>(ivLogo, (String) getText(R.string.tran_logo));
			pairs[1] = new Pair<View, String>(tvWelcome, (String) getText(R.string.tran_title));
			pairs[2] = new Pair<View, String>(tvSignIn, (String) getText(R.string.tran_signInContinue));
			pairs[3] = new Pair<View, String>(tiEmail, (String) getText(R.string.tran_email));
			pairs[4] = new Pair<View, String>(tiPassword, (String) getText(R.string.tran_password));
			pairs[5] = new Pair<View, String>(btGo, (String) getText(R.string.tran_go));
			pairs[6] = new Pair<View, String>(btSignUp, (String) getText(R.string.tran_newUser));

			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignIn.this, pairs);
			startActivity(new Intent(SignIn.this, SignUp.class), options.toBundle());
		});
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (mAuthProvider.getUserLogged()) {
			startActivity(new Intent(SignIn.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
		}
	}

	private void doSignIn() {
		if (Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				& Validations.validateIsEmpty(getApplicationContext(), tiPassword)) {

			progressBar.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			mAuthProvider.signIn(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					startActivity(new Intent(SignIn.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
				} else {
					progressBar.setVisibility(View.GONE);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					Toast.makeText(SignIn.this, getText(R.string.incorrectUsernameOrPassword), Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private void signInGoogle() {
		progressBar.setVisibility(View.VISIBLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
		Intent signInIntent = mGoogleSignInClient.getSignInIntent();
		startActivityForResult(signInIntent, Constants.REQUEST_CODE_GOOGLE);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.REQUEST_CODE_GOOGLE) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			try {
				GoogleSignInAccount account = task.getResult(ApiException.class);
				firebaseAuthWithGoogle(account);
			} catch (ApiException e) {
				Log.w("ERROR", "Google sign in failed", e);
			}
		}
	}

	private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
		mAuthProvider.googleSignIn(account).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				String idUser = mAuthProvider.getUserId();
				checkUserExist(idUser);
			} else {
				progressBar.setVisibility(View.GONE);
				getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
				Toast.makeText(SignIn.this, getText(R.string.couldNotLoginWithGoogle), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void checkUserExist(String idUser) {
		mUserProvider.getUser(idUser).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				startActivity(new Intent(SignIn.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
			} else {
				String fullname = mAuthProvider.getUserName();
				String email = mAuthProvider.getUserEmail();
				String username = Generators.genRandomUsername();

				User user = new User(idUser, fullname, username, email);

				mUserProvider.createUser(user).addOnCompleteListener(task1 -> {
					if (task1.isSuccessful()) {
						startActivity(new Intent(SignIn.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
					} else {
						progressBar.setVisibility(View.GONE);
						getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
						Toast.makeText(SignIn.this, getText(R.string.userCouldNotBeCreated), Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
}