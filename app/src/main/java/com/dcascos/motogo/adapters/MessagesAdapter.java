package com.dcascos.motogo.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.models.database.Message;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.utils.Generators;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {

	private final Context context;
	private final AuthProvider authProvider;

	public MessagesAdapter(FirestoreRecyclerOptions<Message> options, Context context) {
		super(options);
		this.context = context;
		authProvider = new AuthProvider();
	}

	public class ViewHolder extends RecyclerView.ViewHolder {
		private final TextView tvMessage;
		private final TextView tvDateMessage;
		private final ImageView ivMessageViewed;
		private final LinearLayout llMessage;

		public ViewHolder(View view) {
			super(view);
			tvMessage = view.findViewById(R.id.tv_message);
			tvDateMessage = view.findViewById(R.id.tv_dateMessage);
			ivMessageViewed = view.findViewById(R.id.iv_messageViewed);
			llMessage = view.findViewById(R.id.ll_message);
		}
	}

	@Override
	protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Message message) {
		holder.tvMessage.setText(message.getMessageText());
		holder.tvDateMessage.setText(Generators.dateFormater(message.getCreationDate()));

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		if (message.getUserIdSender().equals(authProvider.getUserId())) {
			params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			params.setMargins(150, 5, 0, 5);
			holder.llMessage.setLayoutParams(params);
			holder.llMessage.setPadding(30, 20, 0, 20);
			holder.llMessage.setBackground(context.getDrawable(R.drawable.shape_sender_message));
			holder.ivMessageViewed.setVisibility(View.VISIBLE);
		} else {
			params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			params.setMargins(0, 5, 150, 5);
			holder.llMessage.setLayoutParams(params);
			holder.llMessage.setPadding(30, 20, 30, 20);
			holder.llMessage.setBackground(context.getDrawable(R.drawable.shape_receiver_message));
			holder.ivMessageViewed.setVisibility(View.GONE);
		}

		if (message.isMessageViewed()) {
			holder.ivMessageViewed.setImageResource(R.drawable.ic_check_readed);
		} else {
			holder.ivMessageViewed.setImageResource(R.drawable.ic_check_arrived);
		}
	}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cv_message, parent, false);
		return new ViewHolder(view);
	}

}
