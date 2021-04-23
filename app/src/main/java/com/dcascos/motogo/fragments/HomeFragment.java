package com.dcascos.motogo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.NewPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

	private View view;
	private FloatingActionButton abAdd;

	public HomeFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_home, container, false);

		abAdd = view.findViewById(R.id.ab_add);

		abAdd.setOnClickListener(v -> goToPost());
		return view;
	}

	private void goToPost() {
		startActivity(new Intent(getContext(), NewPost.class));
	}
}