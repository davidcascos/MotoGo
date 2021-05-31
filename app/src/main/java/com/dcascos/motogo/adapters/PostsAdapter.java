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
import com.dcascos.motogo.models.database.Like;
import com.dcascos.motogo.models.database.Post;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.CommentsProvider;
import com.dcascos.motogo.providers.database.LikesProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.dcascos.motogo.utils.Generators;
import com.dcascos.motogo.utils.PostUtils;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.Objects;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

	private final Context context;
	private final UsersProvider usersProvider;
	private final LikesProvider likesProvider;
	private final CommentsProvider commentsProvider;
	private final AuthProvider authProvider;

	public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
		super(options);
		this.context = context;
		usersProvider = new UsersProvider();
		likesProvider = new LikesProvider();
		commentsProvider = new CommentsProvider();
		authProvider = new AuthProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final ImageView ivPostImage;
		private final TextView tvPostTitle;
		private final TextView tvCreationDate;
		private final TextView tvUsername;
		private final TextView tvDescription;
		private final View viewHolder;
		private final ImageView ivLikes;
		private final TextView tvLikes;
		private final TextView tvComments;

		public ViewHolder(View view) {
			super(view);
			ivPostImage = view.findViewById(R.id.iv_postImage);
			tvPostTitle = view.findViewById(R.id.tv_postTitle);
			tvCreationDate = view.findViewById(R.id.tv_creationDate);
			tvUsername = view.findViewById(R.id.tv_username);
			tvDescription = view.findViewById(R.id.tv_description);
			ivLikes = view.findViewById(R.id.iv_like);
			tvLikes = view.findViewById(R.id.tv_likes);
			tvComments = view.findViewById(R.id.tv_comments);
			viewHolder = view;
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post post) {
		DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

		if (post.getImage() != null && !post.getImage().isEmpty()) {
			Glide.with(context).load(post.getImage()).into(holder.ivPostImage);
		}
		holder.tvPostTitle.setText(post.getTitle());
		holder.tvCreationDate.setText(Generators.dateFormater(post.getCreationDate()));
		holder.tvDescription.setText(post.getDescription());
		holder.viewHolder.setOnClickListener(v -> context.startActivity(new Intent(context, PostDetail.class).putExtra("documentId", documentSnapshot.getId())));

		holder.ivLikes.setOnClickListener(v -> {
			Like like = new Like();
			like.setPostId(documentSnapshot.getId());
			like.setUserId(authProvider.getUserId());
			like.setCreationDate(new Date().getTime());
			createLike(like, holder, post.getUserId());
		});

		getUserInfo(post.getUserId(), holder);

		checkLike(documentSnapshot.getId(), authProvider.getUserId(), holder);

		getNumberLikesByPost(documentSnapshot.getId(), holder);
		getNumberCommentsByPost(documentSnapshot.getId(), holder);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_post, parent, false);
		return new ViewHolder(view);
	}

	private void getUserInfo(String userId, PostsAdapter.ViewHolder holder) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists() && documentSnapshot.contains(Constants.USER_USERNAME)) {
				holder.tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
			}
		});
	}

	private void checkLike(String postId, String userId, ViewHolder holder) {
		likesProvider.getLikeByPostAndUser(postId, userId).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (queryDocumentSnapshots.isEmpty()) {
				holder.ivLikes.setImageResource(R.drawable.ic_like_no);
			} else {
				holder.ivLikes.setImageResource(R.drawable.ic_like);
			}
		});
	}

	private void createLike(Like like, ViewHolder holder, String postUserId) {
		likesProvider.getLikeByPostAndUser(like.getPostId(), authProvider.getUserId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (!queryDocumentSnapshots.isEmpty()) {
				likesProvider.delete(queryDocumentSnapshots.getDocuments().get(0).getId());
				holder.ivLikes.setImageResource(R.drawable.ic_like_no);
			} else {
				likesProvider.create(like);
				holder.ivLikes.setImageResource(R.drawable.ic_like);
				PostUtils.sendNotification(context, authProvider.getUserId(), postUserId, Constants.LIKES);
			}
		});
	}

	private void getNumberLikesByPost(String postId, ViewHolder holder) {
		likesProvider.getLikeByPost(postId).addSnapshotListener((value, error) -> {
			int likes = Objects.requireNonNull(value).size();

			holder.tvLikes.setText(context.getString(R.string.likes, String.valueOf(likes)));
		});
	}

	private void getNumberCommentsByPost(String postId, ViewHolder holder) {
		commentsProvider.getCommentsByPost(postId).addSnapshotListener((value, error) -> {
			int comments = Objects.requireNonNull(value).size();

			holder.tvComments.setText(context.getString(R.string.comments, String.valueOf(comments)));
		});
	}
}