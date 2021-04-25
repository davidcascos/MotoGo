package com.dcascos.motogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Comment;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

	Context context;
	UsersProvider usersProvider;

	public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context) {
		super(options);
		this.context = context;
		usersProvider = new UsersProvider();
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

		holder.tvCommentDescription.setText(comment.getCommentText());

		holder.tvDate.setText(Generators.dateFormater(comment.getCreationDate()));
		getUserInfo(comment.getUserId(), holder);
	}

	private void getUserInfo(String userId, ViewHolder holder) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.USER_IMAGEPROFILE)) {
					String imageProfile = documentSnapshot.getString(Constants.USER_IMAGEPROFILE);

					if (imageProfile != null && !imageProfile.isEmpty()) {
						Glide.with(context).load(imageProfile).circleCrop().into(holder.cvProfile);
					}
				}
				if (documentSnapshot.contains(Constants.USER_USERNAME)) {
					holder.tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				}
			}
		});
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_comment, parent, false);
		return new ViewHolder(view);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private CircleImageView cvProfile;
		private TextView tvUsername;
		private TextView tvCommentDescription;
		private TextView tvDate;

		public ViewHolder(View view) {
			super(view);
			cvProfile = view.findViewById(R.id.cv_profile);
			tvUsername = view.findViewById(R.id.tv_username);
			tvCommentDescription = view.findViewById(R.id.tv_commentDescription);
			tvDate = view.findViewById(R.id.tv_date);
		}
	}
}
