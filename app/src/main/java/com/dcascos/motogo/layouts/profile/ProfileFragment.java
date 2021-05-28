package com.dcascos.motogo.layouts.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.ProfileTabsAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.PostsProvider;
import com.dcascos.motogo.providers.RoutesProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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
	private PostsProvider postsProvider;
	private RoutesProvider routesProvider;

	private ViewPager2 viewPager2;
	private ProfileTabsAdapter ProfileTabsAdapter;

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
		postsProvider = new PostsProvider();
		routesProvider = new RoutesProvider();

		llEditProfile.setOnClickListener(v -> goToEditProfile());

		return view;
	}


	@Override
	public void onResume() {
		super.onResume();

		getUserData();
		getPostsAndRoutesCount();
	}

	private void goToEditProfile() {
		startActivity(new Intent(getContext(), ProfileEdit.class));
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

	private void getPostsAndRoutesCount() {
		postsProvider.getPostByUser(authProvider.getUserId()).get().addOnSuccessListener(queryDocumentSnapshotsPosts ->
			routesProvider.getRouteByUser(authProvider.getUserId()).get().addOnSuccessListener(queryDocumentSnapshotsRoutes -> {
			ProfileTabsAdapter = new ProfileTabsAdapter(getActivity(), authProvider.getUserId());
			viewPager2 = view.findViewById(R.id.pager);
			viewPager2.setAdapter(ProfileTabsAdapter);

			TabLayout tabLayout = view.findViewById(R.id.tab_layout);

			new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
				BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
				switch (position) {
					case 1:
						tab.setText(getText(R.string.routes));
						badgeDrawable.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
						badgeDrawable.setVisible(true);
						badgeDrawable.setNumber(queryDocumentSnapshotsRoutes.size());
						badgeDrawable.setMaxCharacterCount(4);
						break;
					default:
						tab.setText(getText(R.string.posts));
						badgeDrawable.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black));
						badgeDrawable.setVisible(true);
						badgeDrawable.setNumber(queryDocumentSnapshotsPosts.size());
						badgeDrawable.setMaxCharacterCount(4);
						break;
				}
			}).attach();
			tvPostsNumber.setText(String.valueOf(queryDocumentSnapshotsPosts.size() + queryDocumentSnapshotsRoutes.size()));
		}));
	}

}