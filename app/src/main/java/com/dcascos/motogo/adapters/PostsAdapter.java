package com.dcascos.motogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.models.Post;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class PostsAdapter extends FirestoreRecyclerAdapter<Post, PostsAdapter.ViewHolder> {

	Context context;

	public PostsAdapter(FirestoreRecyclerOptions<Post> options, Context context) {
		super(options);
		this.context = context;
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Post model) {
		if (model.getImage() != null && !model.getImage().isEmpty()) {
			Glide.with(context).load(model.getImage()).into(holder.ivPostCard);
		}
		holder.tvTitleCard.setText(model.getTitle());
		holder.tvDescriptionCard.setText(model.getDescription());
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_post, parent, false);
		return new ViewHolder(view);
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private ImageView ivPostCard;
		private TextView tvTitleCard;
		private TextView tvDescriptionCard;

		public ViewHolder(View view) {
			super(view);
			ivPostCard = view.findViewById(R.id.iv_postCard);
			tvTitleCard = view.findViewById(R.id.tv_titleCard);
			tvDescriptionCard = view.findViewById(R.id.tv_descriptionCard);
		}
	}
}
