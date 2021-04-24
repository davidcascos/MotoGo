package com.dcascos.motogo.providers;

import com.dcascos.motogo.models.Post;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class PostProvider {

	CollectionReference collectionReference;

	public PostProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection("Posts");
	}

	public Task<Void> save(Post post) {
		return collectionReference.document().set(post);
	}
}
