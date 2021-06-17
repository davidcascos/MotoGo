package com.dcascos.motogo.layouts.login;

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

import com.dcascos.motogo.utils.MainActivity;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.home.Home;
import com.dcascos.motogo.models.database.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class LoginSignIn extends MainActivity {

	private RelativeLayout progressBar;
	private ImageView ivLogo;
	private TextView tvWelcome;
	private TextView tvSignIn;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;
	private Button btGo;
	private Button btSignUp;
	private Button btForgetPassword;
	private SignInButton btGoogle;

	private AuthProvider authProvider;
	private ImageProvider imageProvider;
	private UsersProvider usersProvider;
	private GoogleSignInClient googleSignInClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_login_signin);

		progressBar = findViewById(R.id.rl_progress);
		ivLogo = findViewById(R.id.iv_logo);
		tvWelcome = findViewById(R.id.tv_welcome);
		tvSignIn = findViewById(R.id.tv_signin);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);
		btGo = findViewById(R.id.bt_go);
		btSignUp = findViewById(R.id.bt_signUp);
		btForgetPassword = findViewById(R.id.bt_forgetPassword);
		btGoogle = findViewById(R.id.bt_google);
		btGoogle.setSize(SignInButton.SIZE_WIDE);

		authProvider = new AuthProvider();
		imageProvider = new ImageProvider();
		usersProvider = new UsersProvider();

		// Configure Google Sign In
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
		googleSignInClient = GoogleSignIn.getClient(this, gso);

		btGo.setOnClickListener(v -> doSignIn());
		btGoogle.setOnClickListener(v -> signInGoogle());
		btForgetPassword.setOnClickListener(v -> startActivity(new Intent(LoginSignIn.this, LoginPasswordReset.class)));

		btSignUp.setOnClickListener(v -> {
			Pair[] pairs = new Pair[7];
			pairs[0] = new Pair<View, String>(ivLogo, (String) getText(R.string.tran_logo));
			pairs[1] = new Pair<View, String>(tvWelcome, (String) getText(R.string.tran_title));
			pairs[2] = new Pair<View, String>(tvSignIn, (String) getText(R.string.tran_signInContinue));
			pairs[3] = new Pair<View, String>(tiEmail, (String) getText(R.string.tran_email));
			pairs[4] = new Pair<View, String>(tiPassword, (String) getText(R.string.tran_password));
			pairs[5] = new Pair<View, String>(btGo, (String) getText(R.string.tran_go));
			pairs[6] = new Pair<View, String>(btSignUp, (String) getText(R.string.tran_newUser));

			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginSignIn.this, pairs);
			startActivity(new Intent(LoginSignIn.this, LoginSignUp.class), options.toBundle());
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		progressBar.setVisibility(View.GONE);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (authProvider.getUserLogged()) {
			goHome();
		}
	}

	private void doSignIn() {
		if (Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				& Validations.validateIsEmpty(getApplicationContext(), tiPassword)) {

			showProgressBar();

			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			authProvider.signIn(email, password).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					goHome();
				} else {
					hideProgressBar();
					Toast.makeText(LoginSignIn.this, getText(R.string.incorrectUsernameOrPassword), Toast.LENGTH_LONG).show();
				}
			});
		}
	}

	private void signInGoogle() {
		showProgressBar();
		Intent signInIntent = googleSignInClient.getSignInIntent();
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
		authProvider.googleSignIn(account).addOnCompleteListener(this, task -> {
			if (task.isSuccessful()) {
				String userId = authProvider.getUserId();
				checkUserExist(userId);
			} else {
				hideProgressBar();
				Toast.makeText(LoginSignIn.this, getText(R.string.couldNotLoginWithGoogle), Toast.LENGTH_LONG).show();
			}
		});
	}

	private void checkUserExist(String userId) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				goHome();
			} else {
				imageProvider.saveCoverWithoutImage().getDownloadUrl().addOnSuccessListener(uriCover ->
						imageProvider.saveProfileWithoutImage().getDownloadUrl().addOnSuccessListener(uriProfile -> {
							String fullname = authProvider.getUserName();
							String email = authProvider.getUserEmail();

							usersProvider.checkUsernameExistsToGenerateRandom().get().addOnCompleteListener(task -> {
								if (task.isSuccessful()) {

									List<String> usernames = new ArrayList<>();
									for (QueryDocumentSnapshot document : task.getResult()) {
										usernames.add(document.getString(Constants.USER_USERNAME));
									}

									String username;
									do {
										username = Generators.genRandomUsername();
									} while (usernames.contains(username));

									User user = new User();
									user.setId(userId);
									user.setFullName(fullname);
									user.setUsername(username);
									user.setEmail(email);
									user.setImageCover(uriCover.toString());
									user.setImageProfile(uriProfile.toString());
									user.setCreationDate(new Date().getTime());
									user.setModificationDate(user.getCreationDate());
									usersProvider.createUser(user).addOnCompleteListener(task1 -> {
										if (task1.isSuccessful()) {
											goHome();
										} else {
											hideProgressBar();
											Toast.makeText(LoginSignIn.this, getText(R.string.userCouldNotBeCreated), Toast.LENGTH_SHORT).show();
										}
									});
								}
							});
						}));
			}
		});
	}

	private void goHome() {
		startActivity(new Intent(LoginSignIn.this, Home.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	private void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}

	private void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}
}