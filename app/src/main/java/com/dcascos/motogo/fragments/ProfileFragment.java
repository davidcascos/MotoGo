package com.dcascos.motogo.fragments;

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
import com.dcascos.motogo.layouts.EditProfile;
import com.dcascos.motogo.providers.AuthProvider;
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

	private AuthProvider authProvider;
	private UsersProvider usersProvider;

	public ProfileFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fr_profile, container, false);

		llEditProfile = view.findViewById(R.id.ll_editProfile);
		ivCover = view.findViewById(R.id.iv_cover);
		circleImageProfile = view.findViewById(R.id.circleImageProfile);
		tvFullName = view.findViewById(R.id.tv_fullName);
		tvUsername = view.findViewById(R.id.tv_username);
		tvEmail = view.findViewById(R.id.tv_email);

		authProvider = new AuthProvider();
		usersProvider = new UsersProvider();

		getUserData();

		llEditProfile.setOnClickListener(v -> goToEditProfile());

		return view;
	}

	private void goToEditProfile() {
		startActivity(new Intent(getContext(), EditProfile.class));
	}


	private void getUserData() {
		usersProvider.getUser(authProvider.getUserId()).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				tvFullName.setText(documentSnapshot.getString(Constants.FULLNAME));
				tvUsername.setText(documentSnapshot.getString(Constants.USERNAME));
				tvEmail.setText(documentSnapshot.getString(Constants.EMAIL));
				Glide.with(this).load(documentSnapshot.getString(Constants.IMAGECOVER)).into(ivCover);
				Glide.with(this).load(documentSnapshot.getString(Constants.IMAGEPROFILE)).circleCrop().into(circleImageProfile);

				ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
				circleImageProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
		});
	}

}