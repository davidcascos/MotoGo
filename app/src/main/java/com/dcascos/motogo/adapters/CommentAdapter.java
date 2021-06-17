package com.dcascos.motogo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.profile.ProfileFromUser;
import com.dcascos.motogo.models.database.Comment;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends FirestoreRecyclerAdapter<Comment, CommentAdapter.ViewHolder> {

	private final Context context;
	private final UsersProvider usersProvider;

	public CommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context) {
		super(options);
		this.context = context;
		usersProvider = new UsersProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final CircleImageView civProfile;
		private final TextView tvUsername;
		private final TextView tvText;
		private final TextView tvCreationDate;

		public ViewHolder(View view) {
			super(view);
			civProfile = view.findViewById(R.id.civ_profile);
			tvUsername = view.findViewById(R.id.tv_username);
			tvText = view.findViewById(R.id.tv_text);
			tvCreationDate = view.findViewById(R.id.tv_creationDate);
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {
		holder.tvText.setText(comment.getText());

		holder.tvCreationDate.setText(Generators.dateFormater(comment.getCreationDate()));
		getUserInfo(comment.getUserId(), holder);

		holder.civProfile.setOnClickListener(v -> context.startActivity(new Intent(context, ProfileFromUser.class).putExtra("userToViewId", comment.getUserId())));
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_comment, parent, false);
		return new ViewHolder(view);
	}

	private void getUserInfo(String userId, ViewHolder holder) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.USER_IMAGEPROFILE)) {
					String imageProfile = documentSnapshot.getString(Constants.USER_IMAGEPROFILE);

					if (imageProfile != null && !imageProfile.isEmpty()) {
						Glide.with(context).load(imageProfile).circleCrop().into(holder.civProfile);
					}
				}
				if (documentSnapshot.contains(Constants.USER_USERNAME)) {
					holder.tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				}
			}
		});
	}
}
