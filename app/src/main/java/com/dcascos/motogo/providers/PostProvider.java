package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostProvider {

	CollectionReference collectionReference;

	public PostProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.POSTS);
	}

	public Task<Void> save(Post post) {
		return collectionReference.document().set(post);
	}

	public Query getAll() {
		return collectionReference.orderBy(Constants.POST_CREATIONDATE, Query.Direction.DESCENDING);
	}

	public Query getPostByUser(String userId) {
		return collectionReference.whereEqualTo(Constants.POST_USERID, userId);
	}
}