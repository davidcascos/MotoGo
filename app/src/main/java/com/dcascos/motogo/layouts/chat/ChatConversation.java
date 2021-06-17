package com.dcascos.motogo.layouts.chat;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.MessagesAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Chat;
import com.dcascos.motogo.models.database.Message;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.ChatsProvider;
import com.dcascos.motogo.providers.database.MessagesProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatConversation extends AppCompatActivity {

	private ImageButton ibBack;
	private CircleImageView circleImageProfile;
	private TextView tvUsername;
	private TextView tvdateLastChat;
	private ImageButton ibSend;
	private EditText etMessage;

	private String userId1;
	private String userId2;
	private String chatId;
	private String userIdSender;
	private String userIdReciver;

	private AuthProvider authProvider;
	private UsersProvider usersProvider;
	private ChatsProvider chatsProvider;
	private MessagesProvider messagesProvider;

	private RecyclerView rvMessages;
	private MessagesAdapter messagesAdapter;

	LinearLayoutManager linearLayoutManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_chat);

		ibBack = findViewById(R.id.ib_back);
		circleImageProfile = findViewById(R.id.civ_profile);
		tvUsername = findViewById(R.id.tv_username);
		tvdateLastChat = findViewById(R.id.tv_dateLastChat);
		ibSend = findViewById(R.id.ib_send);
		etMessage = findViewById(R.id.et_message);

		rvMessages = findViewById(R.id.rv_messages);

		getIntentExtras();

		authProvider = new AuthProvider();
		usersProvider = new UsersProvider();
		chatsProvider = new ChatsProvider();
		messagesProvider = new MessagesProvider();

		ibSend.setOnClickListener(v -> sendMessage());
		ibBack.setOnClickListener(v -> this.onBackPressed());

		getChatInfo();
		checkIfChatExist();

		linearLayoutManager = new LinearLayoutManager(ChatConversation.this);
		linearLayoutManager.setStackFromEnd(true);
		rvMessages.setLayoutManager(linearLayoutManager);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (messagesAdapter != null) {
			messagesAdapter.startListening();
		}
	}

	@Override
	public void onStop() {
		super.onStop();
		messagesAdapter.stopListening();
	}

	private void getIntentExtras() {
		userId1 = getIntent().getStringExtra("userId1");
		userId2 = getIntent().getStringExtra("userId2");
	}

	private void getChatInfo() {
		if (authProvider.getUserId().equals(userId1)) {
			userIdSender = userId1;
			userIdReciver = userId2;
		} else {
			userIdSender = userId2;
			userIdReciver = userId1;
		}


		usersProvider.getUser(userIdReciver).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.USER_IMAGEPROFILE)) {
					String imageProfile = documentSnapshot.getString(Constants.USER_IMAGEPROFILE);

					if (imageProfile != null && !imageProfile.isEmpty()) {
						Glide.with(this).load(imageProfile).circleCrop().into(circleImageProfile);
					}
				}
				if (documentSnapshot.contains(Constants.USER_USERNAME)) {
					tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				}
			}
		});
	}

	private void checkIfChatExist() {
		chatsProvider.getChatByListUsersIds(userId1, userId2).get().addOnSuccessListener(queryDocumentSnapshots -> {
			if (queryDocumentSnapshots.size() == 0) {
				chatId = userId1 + userId2;
				createChat();
			} else {
				chatId = queryDocumentSnapshots.getDocuments().get(0).getId();
			}
			getMessages();
		});
	}

	private void createChat() {
		Chat chat = new Chat();
		chat.setId(chatId);
		chat.setUserId1(userId1);
		chat.setUserId2(userId2);
		chat.setWriting(false);

		ArrayList<String> listUsersIds = new ArrayList<>();
		listUsersIds.add(userId1);
		listUsersIds.add(userId2);
		chat.setListUsersIds(listUsersIds);

		chat.setCreationDate(new Date().getTime());
		chat.setModificationDate(chat.getCreationDate());

		chatsProvider.create(chat);
	}

	private void getMessages() {
		Query query = messagesProvider.getMessagesByChatId(chatId);
		FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
		messagesAdapter = new MessagesAdapter(options, ChatConversation.this);
		messagesAdapter.notifyDataSetChanged();
		rvMessages.setAdapter(messagesAdapter);
		messagesAdapter.startListening();
		messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
			@Override
			public void onItemRangeInserted(int positionStart, int itemCount) {
				super.onItemRangeInserted(positionStart, itemCount);
				updateViewed();
				int countMessages = messagesAdapter.getItemCount();
				int lastMessagePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();

				if (lastMessagePosition == -1
						|| (positionStart >= (countMessages - 1) && lastMessagePosition == (positionStart - 1))) {
					rvMessages.scrollToPosition(positionStart);
				}
			}
		});
	}

	private void sendMessage() {
		String messageText = etMessage.getText().toString().trim();
		if (!messageText.isEmpty()) {
			Message message = new Message();
			message.setUserIdSender(userIdSender);
			message.setUserIdReciver(userIdReciver);
			message.setChatId(chatId);
			message.setMessageText(messageText);
			message.setMessageViewed(false);
			message.setCreationDate(new Date().getTime());

			messagesProvider.create(message).addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					chatsProvider.update(chatId, message.getCreationDate());
					etMessage.setText("");
				}
			});
		}
	}

	private void updateViewed() {
		messagesProvider.getMessagesByChatIdAndUserIdReceiverHasSent(chatId, userIdReciver).get().addOnSuccessListener(queryDocumentSnapshots -> {
			for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
				messagesProvider.update(documentSnapshot.getId(), true);
			}
		});
	}

}