package com.dcascos.motogo.layouts.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.ProfileTabsAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.login.LoginSignIn;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.TokenProvider;
import com.dcascos.motogo.providers.database.PostsProvider;
import com.dcascos.motogo.providers.database.RoutesProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
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
	private TokenProvider tokenProvider;

	private ViewPager2 viewPager2;
	private ProfileTabsAdapter profileTabsAdapter;

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

		Toolbar toolbar = view.findViewById(R.id.toolbar);
		((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
		((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.empty));
		setHasOptionsMenu(true);

		authProvider = new AuthProvider();
		usersProvider = new UsersProvider();
		postsProvider = new PostsProvider();
		routesProvider = new RoutesProvider();
		tokenProvider = new TokenProvider();

		llEditProfile.setOnClickListener(v -> goToEditProfile());

		return view;
	}

	@Override
	public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
		inflater.inflate(R.menu.profile_options_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.logout) {
			showConfirmLogOut();
		}
		return super.onOptionsItemSelected(item);
	}

	private void showConfirmLogOut() {
		new AlertDialog.Builder(getContext()).setIcon(R.drawable.ic_logout)
				.setTitle("Log Out")
				.setMessage(R.string.youSureLogOut)
				.setPositiveButton("Yes", (dialog, which) -> {
					tokenProvider.delete(authProvider.getUserId());
					authProvider.signOut();
					startActivity(new Intent(getContext(), LoginSignIn.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
				})
				.setNegativeButton("Cancell", null)
				.show();
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
					profileTabsAdapter = new ProfileTabsAdapter(getActivity(), authProvider.getUserId());
					viewPager2 = view.findViewById(R.id.pager);
					viewPager2.setAdapter(profileTabsAdapter);

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