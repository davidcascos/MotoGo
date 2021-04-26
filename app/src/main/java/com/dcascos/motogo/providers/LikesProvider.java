package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LikesProvider {

	final CollectionReference collectionReference;

	public LikesProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.LIKES);
	}

	public Task<Void> create(Like like) {
		DocumentReference documentReference = collectionReference.document();
		like.setId(documentReference.getId());
		return documentReference.set(like);
	}

	public Task<Void> delete(String likeId) {
		return collectionReference.document(likeId).delete();
	}

	public Query getLikeByPost(String postId) {
		return collectionReference.whereEqualTo(Constants.LIKE_POSTID, postId);
	}

	public Query getLikeByPostAndUser(String postId, String userId) {
		return collectionReference.whereEqualTo(Constants.LIKE_POSTID, postId).whereEqualTo(Constants.LIKE_USERID, userId);
	}
}
