package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class CommentProvider {

	final CollectionReference collectionReference;

	public CommentProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.COMMENTS);
	}

	public Task<Void> create(Comment comment) {
		DocumentReference documentReference = collectionReference.document();
		comment.setId(documentReference.getId());
		return documentReference.set(comment);
	}

	public Query getCommentsByPost(String postId) {
		return collectionReference.whereEqualTo(Constants.COMMENT_POSTID, postId);
	}

}
