package com.dcascos.motogo.providers.database;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PostsProvider {

	final CollectionReference collectionReference;

	public PostsProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.POSTS);
	}

	public Task<Void> create(Post post) {
		DocumentReference documentReference = collectionReference.document();
		post.setId(documentReference.getId());
		return documentReference.set(post);
	}

	public Task<Void> delete(String postId) {
		return collectionReference.document(postId).delete();
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

	public Query getPostByTitle(String title) {
		return collectionReference.orderBy(Constants.POST_TITLE).startAt(title).endAt(title + '\uf8ff');
	}

}
