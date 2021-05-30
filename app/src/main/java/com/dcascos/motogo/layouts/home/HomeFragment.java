package com.dcascos.motogo.layouts.home;

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
import com.dcascos.motogo.layouts.posts.PostNew;
import com.dcascos.motogo.models.Post;
import com.dcascos.motogo.providers.PostsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener {

	private RecyclerView recyclerView;
	private PostsAdapter postsAdapter;
	private PostsAdapter postsAdapterSearch;
	private PostsProvider postsProvider;

	public HomeFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fr_home, container, false);

		FloatingActionButton btAddPost = view.findViewById(R.id.bt_addPost);
		recyclerView = view.findViewById(R.id.rv_home);

		MaterialSearchBar materialSearchBar = view.findViewById(R.id.searchBar);
		materialSearchBar.setOnSearchActionListener(this);

		postsProvider = new PostsProvider();

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);

		btAddPost.setOnClickListener(v -> goToCreatePost());
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getAllPosts();
	}

	@Override
	public void onStop() {
		super.onStop();
		postsAdapter.stopListening();
		if (postsAdapterSearch != null) {
			postsAdapterSearch.stopListening();
		}
	}

	private void goToCreatePost() {
		startActivity(new Intent(getContext(), PostNew.class));
	}

	private void getAllPosts() {
		Query query = postsProvider.getAll();
		FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
		postsAdapter = new PostsAdapter(options, getContext());
		postsAdapter.notifyDataSetChanged();
		recyclerView.setAdapter(postsAdapter);
		postsAdapter.startListening();
	}

	private void searchByTitle(String title) {
		Query query = postsProvider.getPostByTitle(title);
		FirestoreRecyclerOptions<Post> options = new FirestoreRecyclerOptions.Builder<Post>().setQuery(query, Post.class).build();
		postsAdapterSearch = new PostsAdapter(options, getContext());
		postsAdapterSearch.notifyDataSetChanged();
		recyclerView.setAdapter(postsAdapterSearch);
		postsAdapterSearch.startListening();
	}

	@Override
	public void onSearchStateChanged(boolean enabled) {
		if (!enabled) {
			getAllPosts();
		}
	}

	@Override
	public void onSearchConfirmed(CharSequence text) {
		searchByTitle(text.toString());
	}

	@Override
	public void onButtonClicked(int buttonCode) {

	}
}