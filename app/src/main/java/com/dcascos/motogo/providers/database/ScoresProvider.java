package com.dcascos.motogo.providers.database;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Score;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class ScoresProvider {

	final CollectionReference collectionReference;

	public ScoresProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.SCORES);
	}

	public Task<Void> create(Score score) {
		DocumentReference documentReference = collectionReference.document();
		score.setId(documentReference.getId());
		return documentReference.set(score);
	}

	public Task<Void> delete(String routeId) {
		return collectionReference.document(routeId).delete();
	}

	public Task<Void> update(String scoreId, float scoreNum, long modificationDate) {
		Map<String, Object> map = new HashMap<>();
		map.put(Constants.SCORE_NUMBER, scoreNum);
		map.put(Constants.SCORE_CREATIONDATE, modificationDate);
		return collectionReference.document(scoreId).update(map);
	}

	public Query getScoreByRoute(String routeId) {
		return collectionReference.whereEqualTo(Constants.SCORE_ROUTEID, routeId);
	}

	public Query getScoreByRouteAndUser(String routeId, String userId) {
		return collectionReference.whereEqualTo(Constants.SCORE_ROUTEID, routeId).whereEqualTo(Constants.SCORE_USERID, userId);
	}

}
