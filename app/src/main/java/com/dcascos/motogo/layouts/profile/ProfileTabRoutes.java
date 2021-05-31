package com.dcascos.motogo.layouts.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.ProfileRoutesAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Route;
import com.dcascos.motogo.providers.database.RoutesProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ProfileTabRoutes extends Fragment {

	private View view;

	private RecyclerView recyclerView;
	private ProfileRoutesAdapter profileRoutesAdapter;
	private RoutesProvider routesProvider;

	private TextView tvEmptyRoutes;

	private String userId;

	public ProfileTabRoutes() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_profile_tab_routes, container, false);

		userId = getArguments().getString("userId");

		recyclerView = view.findViewById(R.id.rv_profileTabsRoutes);
		routesProvider = new RoutesProvider();

		tvEmptyRoutes = view.findViewById(R.id.tv_emptyRoutes);

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);

		checkIfExistPostByUser();

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Query query = routesProvider.getRouteByUser(userId).orderBy(Constants.POST_CREATIONDATE, Query.Direction.DESCENDING);
		FirestoreRecyclerOptions<Route> options = new FirestoreRecyclerOptions.Builder<Route>().setQuery(query, Route.class).build();
		profileRoutesAdapter = new ProfileRoutesAdapter(options, getContext());
		recyclerView.setAdapter(profileRoutesAdapter);
		profileRoutesAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		profileRoutesAdapter.stopListening();
	}

	private void checkIfExistPostByUser() {
		routesProvider.getRouteByUser(userId).addSnapshotListener((value, error) -> {
			if (value.isEmpty()) {
				tvEmptyRoutes.setVisibility(View.VISIBLE);
			} else {
				tvEmptyRoutes.setVisibility(View.GONE);
			}
		});
	}
}