package com.dcascos.motogo.providers.database;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

	private final CollectionReference collectionReference;

	public UsersProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.USERS);
	}

	public Task<DocumentSnapshot> getUser(String userId) {
		return collectionReference.document(userId).get();
	}

	public Query checkUsernameExistsToGenerateRandom() {
		return collectionReference.orderBy(Constants.USER_USERNAME);
	}

	public Query checkUsernameExists(String username) {
		return collectionReference.whereEqualTo(Constants.USER_USERNAME, username);
	}

	public Query checkUsernameExistsAndNotIsMine(String username, String userId) {
		return collectionReference.whereEqualTo(Constants.USER_USERNAME, username).whereNotEqualTo(Constants.USER_ID, userId);
	}

	public Task<Void> createUser(User user) {
		return collectionReference.document(user.getId()).set(user);
	}

	public Task<Void> update(User user) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.USER_FULLNAME, user.getFullName());
		map.put(Constants.USER_USERNAME, user.getUsername());

		if (user.getImageCover() != null) {
			map.put(Constants.USER_IMAGECOVER, user.getImageCover());
		}
		if (user.getImageProfile() != null) {
			map.put(Constants.USER_IMAGEPROFILE, user.getImageProfile());
		}

		return collectionReference.document(user.getId()).update(map);
	}
}
