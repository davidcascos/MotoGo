package com.dcascos.motogo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.posts.PostDetail;
import com.dcascos.motogo.models.Like;
import com.dcascos.motogo.models.Post;
import com.dcascos.motogo.providers.LikesProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

	Context context;
	UsersProvider usersProvider;
	LikesProvider likesProvider;

	public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
		super(options);
		this.context = context;
		usersProvider = new UsersProvider();
		likesProvider = new LikesProvider();
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {

		DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

		if (post.getImage() != null && !post.getImage().isEmpty()) {
			Glide.with(context).load(post.getImage()).into(holder.ivPostCard);
		}
		holder.tvTitleCard.setText(post.getTitle());
		holder.tvDate.setText(Generators.dateFormater(post.getCreationDate()));
		holder.tvDescriptionCard.setText(post.getDescription());
		holder.viewHolder.setOnClickListener(v -> context.startActivity(new Intent(context, PostDetail.class).putExtra("documentId", documentSnapshot.getId())));

		holder.ivLikes.setOnClickListener(v -> {
			Like like = new Like();
			like.setPostId(documentSnapshot.getId());
			like.setUserId(post.getUserId());
			like.setCreationDate(new Date().getTime());

			createLike(like, holder);
		});

		getUserInfo(post.getUserId(), holder);
		checkLike(documentSnapshot.getId(), post.getUserId(), holder);
		getNumberLikesByPost(documentSnapshot.getId(), holder);
	}

	private void checkLike(String postId, String userId, ViewHolder holder) {
		likesProvider.getLikeByPostAndUser(postId, userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (queryDocumentSnapshots.isEmpty()) {
				holder.ivLikes.setImageResource(R.drawable.ic_love_empty);
			} else {
				holder.ivLikes.setImageResource(R.drawable.ic_love_red);
			}
		});
	}

	private void createLike(Like like, ViewHolder holder) {
		likesProvider.getLikeByPostAndUser(like.getPostId(), like.getUserId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (!queryDocumentSnapshots.isEmpty()) {
				likesProvider.delete(queryDocumentSnapshots.getDocuments().get(0).getId());
				holder.ivLikes.setImageResource(R.drawable.ic_love_empty);
			} else {
				likesProvider.create(like);
				holder.ivLikes.setImageResource(R.drawable.ic_love_red);
			}
		});
	}

	private void getNumberLikesByPost(String postId, ViewHolder holder) {
		likesProvider.getLikeByPost(postId).addSnapshotListener((value, error) -> {
			int likes = value.size();

			holder.tvLikes.setText(context.getString(R.string.likes, String.valueOf(likes)));

		});
	}

	private void getUserInfo(String userId, PostsAdapter.ViewHolder holder) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.USER_USERNAME)) {
					holder.tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				}
			}
		});
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_post, parent, false);
		return new ViewHolder(view);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView ivPostCard;
		private TextView tvTitleCard;
		private TextView tvDate;
		private TextView tvUsername;
		private TextView tvDescriptionCard;
		private View viewHolder;
		private ImageView ivLikes;
		private TextView tvLikes;

		public ViewHolder(View view) {
			super(view);
			ivPostCard = view.findViewById(R.id.iv_postCard);
			tvTitleCard = view.findViewById(R.id.tv_titleCard);
			tvDate = view.findViewById(R.id.tv_date);
			tvUsername = view.findViewById(R.id.tv_username);
			tvDescriptionCard = view.findViewById(R.id.tv_descriptionCard);
			ivLikes = view.findViewById(R.id.iv_like);
			tvLikes = view.findViewById(R.id.tv_likes);
			viewHolder = view;
		}
	}
}