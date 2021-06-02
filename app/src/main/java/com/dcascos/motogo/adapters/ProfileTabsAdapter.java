package com.dcascos.motogo.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.dcascos.motogo.layouts.profile.ProfileTabPosts;
import com.dcascos.motogo.layouts.profile.ProfileTabRoutes;

public class ProfileTabsAdapter extends FragmentStateAdapter {

	private String userId;

	public ProfileTabsAdapter(@NonNull FragmentActivity fragmentActivity, String userId) {
		super(fragmentActivity);
		this.userId = userId;
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		Bundle bundle = new Bundle();

		switch (position) {
			case 1:
				bundle.putString("userId", userId);
				ProfileTabRoutes profileTabRoutes = new ProfileTabRoutes();
				profileTabRoutes.setArguments(bundle);
				return profileTabRoutes;
			default:
				bundle.putString("userId", userId);
				ProfileTabPosts profileTabPosts = new ProfileTabPosts();
				profileTabPosts.setArguments(bundle);
				return profileTabPosts;
		}
	}

	@Override
	public int getItemCount() {
		return 2;
	}
}
