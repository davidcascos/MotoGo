package com.dcascos.motogo.layouts.profile;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.PostsProvider;
import com.dcascos.motogo.providers.UsersProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFromUser extends AppCompatActivity {

	private ImageView ivCover;
	private CircleImageView circleImageProfile;
	private TextView tvFullName;
	private TextView tvUsername;
	private TextView tvEmail;
	private TextView tvPostsNumber;
	private ImageButton ibBack;

	private String extraUserId;

	private UsersProvider usersProvider;
	private PostsProvider postsProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_profile_from_user);

		ivCover = findViewById(R.id.iv_cover);
		circleImageProfile = findViewById(R.id.circleImageProfile);
		tvFullName = findViewById(R.id.tv_fullName);
		tvUsername = findViewById(R.id.tv_username);
		tvEmail = findViewById(R.id.tv_email);
		tvPostsNumber = findViewById(R.id.tv_postsNumber);
		ibBack = findViewById(R.id.ib_back);

		extraUserId = getIntent().getStringExtra("userId");

		usersProvider = new UsersProvider();
		postsProvider = new PostsProvider();

		ibBack.setOnClickListener(v -> this.onBackPressed());

		getUserData();
		getPostCount();
	}

	private void getUserData() {
		usersProvider.getUser(extraUserId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				tvFullName.setText(documentSnapshot.getString(Constants.USER_FULLNAME));
				tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				tvEmail.setText(documentSnapshot.getString(Constants.USER_EMAIL));
				Glide.with(this).load(documentSnapshot.getString(Constants.USER_IMAGECOVER)).into(ivCover);
				Glide.with(this).load(documentSnapshot.getString(Constants.USER_IMAGEPROFILE)).circleCrop().into(circleImageProfile);

				ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
				circleImageProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
		});
	}

	private void getPostCount() {
		postsProvider.getPostByUser(extraUserId).get().addOnSuccessListener(queryDocumentSnapshots ->
				tvPostsNumber.setText(String.valueOf(queryDocumentSnapshots.size())));
	}
}