package com.dcascos.motogo.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.chat.ChatConversation;
import com.dcascos.motogo.models.database.Chat;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.MessagesProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends FirestoreRecyclerAdapter<Chat, ChatAdapter.ViewHolder> {

	private final Context context;
	private final UsersProvider usersProvider;
	private final AuthProvider authProvider;
	private final MessagesProvider messagesProvider;

	public ChatAdapter(FirestoreRecyclerOptions<Chat> options, Context context) {
		super(options);
		this.context = context;
		usersProvider = new UsersProvider();
		authProvider = new AuthProvider();
		messagesProvider = new MessagesProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final CircleImageView civProfile;
		private final TextView tvUsername;
		private final TextView tvLastMessage;
		private final TextView tvModificationDate;
		private final TextView tvCountMessagesNoReaded;
		private final View viewHolder;
		private FrameLayout flCountMessagesNoReaded;

		public ViewHolder(View view) {
			super(view);
			civProfile = view.findViewById(R.id.civ_profile);
			tvUsername = view.findViewById(R.id.tv_username);
			tvLastMessage = view.findViewById(R.id.tv_lastMessage);
			tvModificationDate = view.findViewById(R.id.tv_modificationDate);
			tvCountMessagesNoReaded = view.findViewById(R.id.tv_countMessagesNoReaded);
			flCountMessagesNoReaded = view.findViewById(R.id.fl_countMessagesNoReaded);
			viewHolder = view;
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
		if (authProvider.getUserId().equals(chat.getUserId1())) {
			getUserInfo(chat.getUserId2(), holder);
		} else {
			getUserInfo(chat.getUserId1(), holder);
		}

		getLastMessage(chat.getId(), holder.tvLastMessage);

		String userIdReciver = authProvider.getUserId().equals(chat.getUserId1()) ? chat.getUserId2() : chat.getUserId1();
		getMessgagesNoReaded(chat.getId(), userIdReciver, holder.tvCountMessagesNoReaded, holder.flCountMessagesNoReaded);

		holder.tvModificationDate.setText(Generators.dateFormater(chat.getModificationDate()));

		holder.viewHolder.setOnClickListener(v -> goToChatConversation(chat.getUserId1(), chat.getUserId2()));
	}

	private void getLastMessage(String chatId, TextView tvLastMessage) {
		messagesProvider.getLastMessageByChatId(chatId).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (queryDocumentSnapshots.size() > 0) {
				String message = queryDocumentSnapshots.getDocuments().get(0).getString(Constants.MESSAGE_TEXT);
				tvLastMessage.setText(message);
			}
		});
	}

	private void getMessgagesNoReaded(String chatId, String userIdReciver, TextView tvCountMessagesNoReaded, FrameLayout flCountMessagesNoReaded) {
		messagesProvider.getMessagesByChatIdAndUserIdReceiverHasSent(chatId, userIdReciver).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (queryDocumentSnapshots.size() > 0) {
				flCountMessagesNoReaded.setVisibility(View.VISIBLE);
				tvCountMessagesNoReaded.setText(String.valueOf(queryDocumentSnapshots.size()));
			} else {
				flCountMessagesNoReaded.setVisibility(View.INVISIBLE);
			}
		});
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

	private void goToChatConversation(String userId1, String userId2) {
		context.startActivity(new Intent(context, ChatConversation.class).putExtra("userId1", userId1).putExtra("userId2", userId2));
	}
}
