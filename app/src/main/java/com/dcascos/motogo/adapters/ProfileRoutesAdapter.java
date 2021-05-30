package com.dcascos.motogo.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.models.Route;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.RoutesProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileRoutesAdapter extends FirestoreRecyclerAdapter<Route, ProfileRoutesAdapter.ViewHolder> {

	private final Context context;
	private final AuthProvider authProvider;
	private final RoutesProvider routesProvider;

	public ProfileRoutesAdapter(FirestoreRecyclerOptions<Route> options, Context context) {
		super(options);
		this.context = context;
		authProvider = new AuthProvider();
		routesProvider = new RoutesProvider();
	}

	public static class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvPostTitle;
		private final TextView tvCreationDate;
		private final TextView tvDescription;
		private final View viewHolder;
		private final ImageView ivDelete;

		public ViewHolder(View view) {
			super(view);
			tvPostTitle = view.findViewById(R.id.tv_postTitle);
			tvCreationDate = view.findViewById(R.id.tv_creationDate);
			tvDescription = view.findViewById(R.id.tv_description);
			ivDelete = view.findViewById(R.id.iv_delete);
			viewHolder = view;
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Route route) {
		DocumentSnapshot documentSnapshot = getSnapshots().getSnapshot(position);

		holder.tvPostTitle.setText(route.getOrigin());
		holder.tvCreationDate.setText(Generators.dateFormater(route.getCreationDate()));
		holder.tvDescription.setText(route.getDestination());
		//holder.viewHolder.setOnClickListener(v -> context.startActivity(new Intent(context, PostDetail.class).putExtra("documentId", documentSnapshot.getId())));

		if (route.getUserId().equals(authProvider.getUserId())) {
			holder.ivDelete.setVisibility(View.VISIBLE);
		} else {
			holder.ivDelete.setVisibility(View.GONE);
		}

		holder.ivDelete.setOnClickListener(v -> showConfirmDelete(documentSnapshot.getId()));
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_profile_route, parent, false);
		return new ViewHolder(view);
	}

	private void showConfirmDelete(String routeId) {
		new AlertDialog.Builder(context).setIcon(android.R.drawable.ic_delete)
				.setTitle("Delete route")
				.setMessage("Are you sure?")
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