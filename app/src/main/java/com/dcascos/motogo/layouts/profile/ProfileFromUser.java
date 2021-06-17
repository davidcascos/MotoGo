package com.dcascos.motogo.layouts.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.ProfileTabsAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.chat.ChatConversation;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.PostsProvider;
import com.dcascos.motogo.providers.database.RoutesProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.dcascos.motogo.utils.MainActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFromUser extends MainActivity {

	private ImageView ivCover;
	private CircleImageView circleImageProfile;
	private TextView tvFullName;
	private TextView tvUsername;
	private TextView tvEmail;
	private TextView tvPostsNumber;

	private String userToViewId;

	private UsersProvider usersProvider;
	private PostsProvider postsProvider;
	private RoutesProvider routesProvider;
	private AuthProvider authProvider;

	private ViewPager2 viewPager2;
	private ProfileTabsAdapter ProfileTabsAdapter;

	private FloatingActionButton btCreateChat;

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
		btCreateChat = findViewById(R.id.bt_createChat);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		userToViewId = getIntent().getStringExtra("userToViewId");

		usersProvider = new UsersProvider();
		postsProvider = new PostsProvider();
		routesProvider = new RoutesProvider();
		authProvider = new AuthProvider();

		getUserData();
		getPostsAndRoutesCount();

		if (authProvider.getUserId().equals(userToViewId)) {
			btCreateChat.setVisibility(View.GONE);
		}
		btCreateChat.setOnClickListener(v -> goToChatConversation());
	}

	private void getUserData() {
		usersProvider.getUser(userToViewId).addOnSuccessListener(documentSnapshot -> {
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
		postsProvider.getPostByUser(userToViewId).get().addOnSuccessListener(queryDocumentSnapshotsPosts ->
				routesProvider.getRouteByUser(userToViewId).get().addOnSuccessListener(queryDocumentSnapshotsRoutes -> {
					ProfileTabsAdapter = new ProfileTabsAdapter(this, userToViewId);
					viewPager2 = findViewById(R.id.pager);
					viewPager2.setAdapter(ProfileTabsAdapter);

					TabLayout tabLayout = findViewById(R.id.tab_layout);

					new TabLayoutMediator(tabLayout, viewPager2, (tab, position) -> {
						BadgeDrawable badgeDrawable = tab.getOrCreateBadge();
						switch (position) {
							case 1:
								tab.setText(getText(R.string.routes));
								badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
								badgeDrawable.setVisible(true);
								badgeDrawable.setNumber(queryDocumentSnapshotsRoutes.size());
								badgeDrawable.setMaxCharacterCount(4);
								break;
							default:
								tab.setText(getText(R.string.posts));
								badgeDrawable.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
								badgeDrawable.setVisible(true);
								badgeDrawable.setNumber(queryDocumentSnapshotsPosts.size());
								badgeDrawable.setMaxCharacterCount(4);
								break;
						}
					}).attach();
					tvPostsNumber.setText(String.valueOf(queryDocumentSnapshotsPosts.size() + queryDocumentSnapshotsRoutes.size()));
				}));
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.onBackPressed();
		}
		return true;
	}

	private void goToChatConversation() {
		startActivity(new Intent(ProfileFromUser.this, ChatConversation.class).putExtra("userId1", authProvider.getUserId()).putExtra("userId2", userToViewId));
	}
}