package com.dcascos.motogo.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.layouts.posts.PostDetail;
import com.dcascos.motogo.models.database.Like;
import com.dcascos.motogo.models.database.Post;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.CommentsProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.database.LikesProvider;
import com.dcascos.motogo.providers.database.PostsProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Date;
import java.util.Objects;

public class ProfilePostsAdapter extends FirestoreRecyclerAdapter<Post, ProfilePostsAdapter.ViewHolder> {

	private final Context context;
	private final LikesProvider likesProvider;
	private final CommentsProvider commentsProvider;
	private final AuthProvider authProvider;
	private final PostsProvider postsProvider;
	private final ImageProvider imageProvider;

	public ProfilePostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
		super(options);
		this.context = context;
		likesProvider = new LikesProvider();
		commentsProvider = new CommentsProvider();
		authProvider = new AuthProvider();
		postsProvider = new PostsProvider();
		imageProvider = new ImageProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final ImageView ivPostImage;
		private final TextView tvPostTitle;
		private final TextView tvCreationDate;
		private final TextView tvDescription;
		private final View viewHolder;
		private final ImageView ivLikes;
		private final TextView tvLikes;
		private final TextView tvComments;
		private final ImageView ivDelete;

		public ViewHolder(View view) {
			super(view);
			ivPostImage = view.findViewById(R.id.iv_postImage);
			tvPostTitle = view.findViewById(R.id.tv_postTitle);
			tvCreationDate = view.findViewById(R.id.tv_creationDate);
			tvDescription = view.findViewById(R.id.tv_description);
			ivLikes = view.findViewById(R.id.iv_like);
			tvLikes = view.findViewById(R.id.tv_likes);
			tvComments = view.findViewById(R.id.tv_comments);
			ivDelete = view.findViewById(R.id.iv_delete);
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
			createLike(like, holder);
		});

		if (post.getUserId().equals(authProvider.getUserId())) {
			holder.ivDelete.setVisibility(View.VISIBLE);
		} else {
			holder.ivDelete.setVisibility(View.GONE);
		}

		holder.ivDelete.setOnClickListener(v -> showConfirmDelete(documentSnapshot.getId(), post.getImage()));

		checkLike(documentSnapshot.getId(), authProvider.getUserId(), holder);

		getNumberLikesByPost(documentSnapshot.getId(), holder);
		getNumberCommentsByPost(documentSnapshot.getId(), holder);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_profile_post, parent, false);
		return new ViewHolder(view);
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

	private void createLike(Like like, ViewHolder holder) {
		likesProvider.getLikeByPostAndUser(like.getPostId(), authProvider.getUserId()).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (!queryDocumentSnapshots.isEmpty()) {
				likesProvider.delete(queryDocumentSnapshots.getDocuments().get(0).getId());
				holder.ivLikes.setImageResource(R.drawable.ic_like_no);
			} else {
				likesProvider.create(like);
				holder.ivLikes.setImageResource(R.drawable.ic_like);
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

	private void showConfirmDelete(String postId, String imageUrl) {
		new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_delete)
				.setTitle("Delete post")
				.setMessage("Are you sure?")
				.setPositiveButton("Yes", (dialog, which) -> deletePost(postId, imageUrl))
				.setNegativeButton("Cancel", null)
				.show();
	}

	private void deletePost(String postId, String imageUrl) {
		postsProvider.delete(postId).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				likesProvider.getLikeByPost(postId).get().addOnSuccessListener(queryDocumentSnapshots -> {
					if (!queryDocumentSnapshots.isEmpty()) {
						for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
							likesProvider.delete(documentSnapshot.getId());
						}
					}
				});

				commentsProvider.getCommentsByPost(postId).get().addOnSuccessListener(queryDocumentSnapshots -> {
					if (!queryDocumentSnapshots.isEmpty()) {
						for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
							commentsProvider.delete(documentSnapshot.getId());
						}
					}
				});
				imageProvider.getStorageFromUrl(imageUrl).delete();

				Toast.makeText(context, R.string.postDeleted, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.postCouldNotBeDeleted, Toast.LENGTH_SHORT).show();
			}
		});
	}
}