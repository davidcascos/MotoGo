package com.dcascos.motogo.layouts.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.ChatAdapter;
import com.dcascos.motogo.models.database.Chat;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.ChatsProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

	private View view;
	private ChatAdapter chatAdapter;
	private RecyclerView recyclerView;

	private ChatsProvider chatsProvider;
	private AuthProvider authProvider;

	public ChatFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fr_chat, container, false);

		recyclerView = view.findViewById(R.id.rv_chats);

		chatsProvider = new ChatsProvider();
		authProvider = new AuthProvider();

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
		recyclerView.setLayoutManager(linearLayoutManager);

		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		getChats();
	}

	@Override
	public void onStop() {
		super.onStop();
		chatAdapter.stopListening();
	}

	private void getChats() {
		Query query = chatsProvider.getAll(authProvider.getUserId());
		FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
		chatAdapter = new ChatAdapter(options, getContext());
		recyclerView.setAdapter(chatAdapter);
		chatAdapter.startListening();
	}
}