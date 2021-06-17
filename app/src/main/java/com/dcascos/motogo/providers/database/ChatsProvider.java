package com.dcascos.motogo.providers.database;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsProvider {

	CollectionReference collectionReference;

	public ChatsProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.CHATS);
	}

	public void create(Chat chat) {
		collectionReference.document(chat.getId()).set(chat);
	}

	public Query getChatsByUserId(String userId) {
		return collectionReference.whereArrayContains(Constants.CHAT_LISTUSERSIDS, userId).orderBy(Constants.CHAT_MODIFICATIONDATE, Query.Direction.DESCENDING);
	}

	public Query getChatByListUsersIds(String userId1, String userId2) {
		ArrayList<String> listUsersIds = new ArrayList<>();
		listUsersIds.add(userId1 + userId2);
		listUsersIds.add(userId2 + userId1);
		return collectionReference.whereIn(Constants.CHAT_ID, listUsersIds);
	}

	public Task<Void> update(String chatId, long modificationDate) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.CHAT_MODIFICATIONDATE, modificationDate);
		return collectionReference.document(chatId).update(map);
	}

}
