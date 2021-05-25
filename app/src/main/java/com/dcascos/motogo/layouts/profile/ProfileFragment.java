package com.dcascos.motogo.layouts.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.PostProvider;
import com.dcascos.motogo.providers.UsersProvider;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

	private View view;
	private LinearLayout llEditProfile;
	private ImageView ivCover;
	private CircleImageView circleImageProfile;
	private TextView tvFullName;
	private TextView tvUsername;
	private TextView tvEmail;
	private TextView tvPostsNumber;

	private AuthProvider authProvider;
	private UsersProvider usersProvider;
	private PostProvider postProvider;

	public ProfileFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_profile, container, false);

		llEditProfile = view.findViewById(R.id.ll_editProfile);
		ivCover = view.findViewById(R.id.iv_cover);
		circleImageProfile = view.findViewById(R.id.circleImageProfile);
		tvFullName = view.findViewById(R.id.tv_fullName);
		tvUsername = view.findViewById(R.id.tv_username);
		tvEmail = view.findViewById(R.id.tv_email);
		tvPostsNumber = view.findViewById(R.id.tv_postsNumber);

		authProvider = new AuthProvider();
		usersProvider = new UsersProvider();
		postProvider = new PostProvider();

		llEditProfile.setOnClickListener(v -> goToEditProfile());

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();

		getUserData();
		getPostCount();
	}

	private void goToEditProfile() {
		startActivity(new Intent(getContext(), EditProfile.class));
	}

	private void getUserData() {
		usersProvider.getUser(authProvider.getUserId()).addOnSuccessListener(documentSnapshot -> {
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
		postProvider.getPostByUser(authProvider.getUserId()).get().addOnSuccessListener(queryDocumentSnapshots ->
				tvPostsNumber.setText(String.valueOf(queryDocumentSnapshots.size())));
	}

}