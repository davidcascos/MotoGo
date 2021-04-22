package com.dcascos.motogo.layouts;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.models.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

	private ImageView ivCover;
	private CircleImageView circleImageProfile;

	private TextInputLayout tiFullname;
	private TextInputLayout tiUsername;
	private TextInputLayout tiEmail;
	private TextInputLayout tiPassword;

	private ImageButton ibBack;

	private Button btUpdateProfile;

	private RelativeLayout progressBar;

	private AuthProvider mAuthProvider;
	private UsersProvider mUserProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_edit_profile);

		ivCover = findViewById(R.id.iv_cover);
		circleImageProfile = findViewById(R.id.circleImageProfile);

		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		tiPassword = findViewById(R.id.ti_password);

		ibBack = findViewById(R.id.ib_back);

		btUpdateProfile = findViewById(R.id.bt_updateProfile);

		progressBar = findViewById(R.id.rl_progress);

		mAuthProvider = new AuthProvider();
		mUserProvider = new UsersProvider();

		ibBack.setOnClickListener(v -> this.onBackPressed());

		btUpdateProfile.setOnClickListener(v -> updateProfile());
	}

	private void updateProfile() {
		if (Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
				& Validations.validateUsernameFormat(getApplicationContext(), tiUsername)
				& Validations.validateEmailFormat(getApplicationContext(), tiEmail)
				& Validations.validatePasswordFormat(getApplicationContext(), tiPassword)) {

			progressBar.setVisibility(View.VISIBLE);
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

			String fullname = Objects.requireNonNull(tiFullname.getEditText()).getText().toString().trim();
			String username = Objects.requireNonNull(tiUsername.getEditText()).getText().toString().trim();
			String email = Objects.requireNonNull(tiEmail.getEditText()).getText().toString().trim();
			String password = Objects.requireNonNull(tiPassword.getEditText()).getText().toString().trim();

			String userId = mAuthProvider.getUserId();
			User user = new User(userId, fullname, username, email);

			mUserProvider.update(user).addOnCompleteListener(task1 -> {
				if (task1.isSuccessful()) {
					Toast.makeText(EditProfile.this, "La informacion se actualizo correctamente", Toast.LENGTH_SHORT).show();
				} else {
					progressBar.setVisibility(View.GONE);
					getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
					Toast.makeText(EditProfile.this, getText(R.string.userCouldNotBeUpdated), Toast.LENGTH_SHORT).show();
				}
			});

		}
	}
}