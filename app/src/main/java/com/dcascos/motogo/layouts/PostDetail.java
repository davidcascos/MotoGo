package com.dcascos.motogo.layouts;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.PostProvider;
import com.dcascos.motogo.providers.UsersProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetail extends AppCompatActivity {

	private ImageView ivCover;
	private CircleImageView cvProfile;
	private TextView tvUsername;
	private TextView tvTitle;
	private TextView tvLocation;
	private TextView tvDescription;
	private Button btShowProfile;

	private String extraPostId;


	UsersProvider usersProvider;
	PostProvider postProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_post_detail);

		ivCover = findViewById(R.id.iv_cover);
		cvProfile = findViewById(R.id.cv_profile);
		tvUsername = findViewById(R.id.tv_username);
		tvTitle = findViewById(R.id.tv_title);
		tvLocation = findViewById(R.id.tv_location);
		tvDescription = findViewById(R.id.tv_description);
		btShowProfile = findViewById(R.id.bt_showProfile);

		extraPostId = getIntent().getStringExtra("documentId");

		usersProvider = new UsersProvider();
		postProvider = new PostProvider();

		getPost();
	}

	private void getPost() {
		postProvider.getPostById(extraPostId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.POST_IMAGE)) {
					Glide.with(getApplicationContext()).load(documentSnapshot.getString(Constants.POST_IMAGE)).into(ivCover);
					ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
				}

				if (documentSnapshot.contains(Constants.POST_TITLE)) {
					tvTitle.setText(documentSnapshot.getString(Constants.POST_TITLE));
				}

				if (documentSnapshot.contains(Constants.POST_LOCATION)) {
					tvLocation.setText(documentSnapshot.getString(Constants.POST_LOCATION));
				}

				if (documentSnapshot.contains(Constants.POST_DESCRIPTION)) {
					tvDescription.setText(documentSnapshot.getString(Constants.POST_DESCRIPTION));
				}

				if (documentSnapshot.contains(Constants.POST_USERID)) {
					getUserInfo(documentSnapshot.getString(Constants.POST_USERID));
				}
			}
		});
	}

	private void getUserInfo(String userId) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.USER_IMAGEPROFILE)) {
					Glide.with(getApplicationContext()).load(documentSnapshot.getString(Constants.USER_IMAGEPROFILE)).circleCrop().into(cvProfile);
				}

				if (documentSnapshot.contains(Constants.USER_USERNAME)) {
					tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				}
			}
		});
	}
}