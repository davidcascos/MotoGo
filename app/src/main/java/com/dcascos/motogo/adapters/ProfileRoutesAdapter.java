package com.dcascos.motogo.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.maps.MapsRouteDetail;
import com.dcascos.motogo.models.database.Route;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.ScoresProvider;
import com.dcascos.motogo.providers.database.RoutesProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileRoutesAdapter extends FirestoreRecyclerAdapter<Route, ProfileRoutesAdapter.ViewHolder> {

	private final Context context;
	private final AuthProvider authProvider;
	private final RoutesProvider routesProvider;
	private final ScoresProvider scoresProvider;

	public ProfileRoutesAdapter(FirestoreRecyclerOptions<Route> options, Context context) {
		super(options);
		this.context = context;
		authProvider = new AuthProvider();
		routesProvider = new RoutesProvider();
		scoresProvider = new ScoresProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvRouteOrigin;
		private final TextView tvRouteDestination;
		private final TextView tvCreationDate;
		private final TextView tvScore;
		private final RatingBar rbRouteScore;
		private final View viewHolder;
		private final ImageView ivDelete;

		public ViewHolder(View view) {
			super(view);
			tvRouteOrigin = view.findViewById(R.id.tv_routeOrigin);
			tvRouteDestination = view.findViewById(R.id.tv_routeDestination);
			tvCreationDate = view.findViewById(R.id.tv_creationDate);
			tvScore = view.findViewById(R.id.tv_score);
			rbRouteScore = view.findViewById(R.id.rb_routeScore);
			ivDelete = view.findViewById(R.id.iv_delete);
			viewHolder = view;

			tvRouteOrigin.setSelected(true);
			tvRouteDestination.setSelected(true);
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Route route) {
		DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

		holder.tvRouteOrigin.setText(route.getOrigin());
		holder.tvCreationDate.setText(Generators.dateFormater(route.getCreationDate()));
		holder.tvRouteDestination.setText(route.getDestination());
		holder.viewHolder.setOnClickListener(v -> context.startActivity(new Intent(context, MapsRouteDetail.class)
				.putExtra("from", "detail")
				.putExtra("routeId", route.getId())
				.putExtra("originName", route.getOrigin())
				.putExtra("destinationName", route.getDestination())
				.putExtra("originLat", route.getOriginLat())
				.putExtra("originLon", route.getOriginLon())
				.putExtra("destinationLat", route.getDestinationLat())
				.putExtra("destinationLon", route.getDestinationLon())));

		if (route.getUserId().equals(authProvider.getUserId())) {
			holder.ivDelete.setVisibility(View.VISIBLE);
		} else {
			holder.ivDelete.setVisibility(View.GONE);
		}

		holder.ivDelete.setOnClickListener(v -> showConfirmDelete(documentSnapshot.getId()));

		getNumberScoreByRoute(documentSnapshot.getId(), holder);
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_profile_route, parent, false);
		return new ViewHolder(view);
	}

	private void getNumberScoreByRoute(String routeId, ProfileRoutesAdapter.ViewHolder holder) {
		scoresProvider.getScoreByRoute(routeId).addSnapshotListener((value, error) -> {
			float totalScores = 0;
			float sumScores = 0;

			if (value != null && value.size() > 0) {
				for (int i = 0; i < value.size(); i++) {
					sumScores = sumScores + value.getDocuments().get(i).getDouble(Constants.SCORE_NUMBER).floatValue();
				}
				totalScores = sumScores / value.size();
			}
			holder.tvScore.setText(context.getString(R.string.scores, String.valueOf(totalScores)));
			holder.rbRouteScore.setRating(totalScores);
		});
	}

	private void showConfirmDelete(String routeId) {
		new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_delete)
				.setTitle("Delete route")
				.setMessage(R.string.youSureDeleteRoute)
				.setPositiveButton("Yes", (dialog, which) -> deleteRoute(routeId))
				.setNegativeButton("Cancel", null)
				.show();
	}

	private void deleteRoute(String routeId) {
		routesProvider.delete(routeId).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				Toast.makeText(context, R.string.routeDeleted, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(context, R.string.routeCouldNotBeDeleted, Toast.LENGTH_SHORT).show();
			}
		});
	}

}