package com.dcascos.motogo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.EditProfile;

public class ProfileFragment extends Fragment {

	View view;
	LinearLayout llEditProfile;

	public ProfileFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		view = inflater.inflate(R.layout.fr_profile, container, false);

		llEditProfile = view.findViewById(R.id.ll_editProfile);

		llEditProfile.setOnClickListener(v -> goToEditProfile());

		return view;
	}

	private void goToEditProfile() {
		startActivity(new Intent(getContext(), EditProfile.class));
	}


}