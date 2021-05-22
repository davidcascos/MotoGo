package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostProvider {

	final CollectionReference collectionReference;

	public PostProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.POSTS);
	}

	public Task<Void> create(Post post) {
		DocumentReference documentReference = collectionReference.document();
		post.setId(documentReference.getId());
		return documentReference.set(post);
	}

	public Query getAll() {
		return collectionReference.orderBy(Constants.POST_CREATIONDATE, Query.Direction.DESCENDING);
	}

	public Query getPostByUser(String userId) {
		return collectionReference.whereEqualTo(Constants.POST_USERID, userId);
	}

	public Task<DocumentSnapshot> getPostById(String postId) {
		return collectionReference.document(postId).get();
	}
}
