package com.dcascos.motogo.providers.database;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Message;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class MessagesProvider {

	CollectionReference collectionReference;

	public MessagesProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.MESSAGES);
	}

	public Task<Void> create(Message message) {
		DocumentReference documentReference = collectionReference.document();
		message.setId(documentReference.getId());
		return documentReference.set(message);
	}

	public Query getMessagesByChatId(String chatId) {
		return collectionReference.whereEqualTo(Constants.MESSAGE_CHATID, chatId).orderBy(Constants.MESSAGE_CREATIONDATE, Query.Direction.ASCENDING);
	}

	public Query getMessagesByChatIdAndUserIdReceiverHasSent(String chatId, String userIdSender) {
		return collectionReference.whereEqualTo(Constants.MESSAGE_CHATID, chatId).whereEqualTo(Constants.MESSAGE_USERID_SENDER, userIdSender).whereEqualTo(Constants.MESSAGE_VIEWED, false);
	}

	public Query getLastMessageByChatId(String chatId) {
		return collectionReference.whereEqualTo(Constants.MESSAGE_CHATID, chatId).orderBy(Constants.MESSAGE_CREATIONDATE, Query.Direction.DESCENDING).limit(1);
	}

	public Task<Void> update(String messageId, boolean messageViewed) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.MESSAGE_VIEWED, messageViewed);
		return collectionReference.document(messageId).update(map);
	}
}
