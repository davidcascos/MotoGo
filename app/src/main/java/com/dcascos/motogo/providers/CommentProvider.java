package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class CommentProvider {

	CollectionReference collectionReference;

	public CommentProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.COMMENTS);
	}

	public Task<Void> create(Comment comment) {
		return collectionReference.document().set(comment);
	}
}
