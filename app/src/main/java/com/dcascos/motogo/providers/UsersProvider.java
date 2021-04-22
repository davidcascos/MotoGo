package com.dcascos.motogo.providers;

import com.dcascos.motogo.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class UsersProvider {

	private CollectionReference mCollection;

	public UsersProvider() {
		mCollection = FirebaseFirestore.getInstance().collection("Users");
	}

	public Task<DocumentSnapshot> getUser(String userId) {
		return mCollection.document(userId).get();
	}

	public Task<Void> createUser(User user) {
		return mCollection.document(user.getId()).set(user);
	}

	public Task<Void> update(User user) {
		return mCollection.document(user.getId()).update((Map<String, Object>) user);
	}

}
