package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Token;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class TokenProvider {

	CollectionReference collectionReference;

	public TokenProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.TOKENS);
	}

	public void create(String userId) {
		if (userId == null) {
			return;
		}

		FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> {
			Token token = new Token(s);
			collectionReference.document(userId).set(token);
		});
	}

	public Task<DocumentSnapshot> getToken(String userId) {
		return collectionReference.document(userId).get();
	}
}
