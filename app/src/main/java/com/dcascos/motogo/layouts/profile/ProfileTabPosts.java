package com.dcascos.motogo.layouts.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.ProfilePostsAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Post;
import com.dcascos.motogo.providers.PostsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ProfileTabPosts extends Fragment {

	private View view;

	private RecyclerView recyclerView;
	private ProfilePostsAdapter profilePostsAdapter;
	private PostsProvider postsProvider;

	private TextView tvEmptyPosts;

	private String userId;

	public ProfileTabPosts() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_profile_tab_posts, container, false);

		userId = getArguments().getString("userId");

		recyclerView = view.findViewById(R.id.rv_profileTabsPosts);
		postsProvider = new PostsProvider();

		tvEmptyPosts = view.findViewById(R.id.tv_emptyPosts);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);

		checkIfExistPostByUser();

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Query query = postsProvider.getPostByUser(userId).orderBy(Constants.POST_CREATIONDATE, Query.Direction.DESCENDING);
		FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
		profilePostsAdapter = new ProfilePostsAdapter(options, getContext());
		recyclerView.setAdapter(profilePostsAdapter);
		profilePostsAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		profilePostsAdapter.stopListening();
	}

	private void checkIfExistPostByUser() {
		postsProvider.getPostByUser(userId).addSnapshotListener((value, error) -> {
			if (value.isEmpty()) {
				tvEmptyPosts.setVisibility(View.VISIBLE);
			} else {
				tvEmptyPosts.setVisibility(View.GONE);
			}
		});
	}
}