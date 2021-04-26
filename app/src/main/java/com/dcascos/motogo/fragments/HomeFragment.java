package com.dcascos.motogo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.PostsAdapter;
import com.dcascos.motogo.layouts.posts.NewPost;
import com.dcascos.motogo.models.Post;
import com.dcascos.motogo.providers.PostProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

public class HomeFragment extends Fragment {

	private View view;
	private FloatingActionButton btAddPost;
	private RecyclerView recyclerView;

	private PostsAdapter postsAdapter;

	private PostProvider postProvider;

	public HomeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_home, container, false);

		btAddPost = view.findViewById(R.id.bt_addPost);
		recyclerView = view.findViewById(R.id.rv_home);

		postProvider = new PostProvider();

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);

		btAddPost.setOnClickListener(v -> goToPost());
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Query query = postProvider.getAll();
		FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
		postsAdapter = new PostsAdapter(options, getContext());
		recyclerView.setAdapter(postsAdapter);
		postsAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		postsAdapter.stopListening();
	}

	private void goToPost() {
		startActivity(new Intent(getContext(), NewPost.class));
	}
}