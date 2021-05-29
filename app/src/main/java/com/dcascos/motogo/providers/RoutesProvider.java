package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Route;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RoutesProvider {

	final CollectionReference collectionReference;

	public RoutesProvider() {
		collectionReference = FirebaseFirestore.getInstance().collection(Constants.ROUTES);
	}

	public Task<Void> create(Route route) {
		DocumentReference documentReference = collectionReference.document();
		route.setId(documentReference.getId());
		return documentReference.set(route);
	}

	public Task<Void> delete(String routeId) {
		return collectionReference.document(routeId).delete();
	}

	public Task<QuerySnapshot> getAll() {
		return collectionReference.get();
	}

	public Query getRouteByUser(String userId) {
		return collectionReference.whereEqualTo(Constants.ROUTE_USERID, userId);
	}

}
