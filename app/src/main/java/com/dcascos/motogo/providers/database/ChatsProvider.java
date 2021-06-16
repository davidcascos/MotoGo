package com.dcascos.motogo.providers.database;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Chat;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ChatsProvider {

	CollectionReference collectionReference;

	public ChatsProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.CHATS);
	}

	public void create(Chat chat) {
		collectionReference.document(chat.getUserId1()).collection(Constants.CHAT_USERS).document(chat.getUserId2()).set(chat);
		collectionReference.document(chat.getUserId2()).collection(Constants.CHAT_USERS).document(chat.getUserId1()).set(chat);
	}

	public Query getAll(String userId) {
		return collectionReference.document(userId).collection(Constants.CHAT_USERS);
	}
}
