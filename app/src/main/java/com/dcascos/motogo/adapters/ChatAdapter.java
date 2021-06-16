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
import com.dcascos.motogo.models.database.Chat;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

	private final Context context;
	private final UsersProvider usersProvider;

	public ChatAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
		super(options);
		this.context = context;
		usersProvider = new UsersProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final CircleImageView civProfile;
		private final TextView tvUsername;
		private final TextView tvLastMessage;
		private final TextView tvCreationDate;

		public ViewHolder(View view) {
			super(view);
			civProfile = view.findViewById(R.id.civ_profileChat);
			tvUsername = view.findViewById(R.id.tv_usernameChat);
			tvLastMessage = view.findViewById(R.id.tv_lastMessage);
			tvCreationDate = view.findViewById(R.id.tv_creationDateChat);
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
		getUserInfo(getSnapshots().getSnapshot(position).getId(), holder);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_chat, parent, false);
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
