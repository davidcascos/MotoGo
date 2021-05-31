package com.dcascos.motogo.layouts.posts;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.adapters.CommentAdapter;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.layouts.profile.ProfileFromUser;
import com.dcascos.motogo.models.database.Comment;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.database.CommentsProvider;
import com.dcascos.motogo.providers.database.PostsProvider;
import com.dcascos.motogo.providers.database.UsersProvider;
import com.dcascos.motogo.utils.PostUtils;
import com.dcascos.motogo.utils.Validations;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetail extends AppCompatActivity {

	private RelativeLayout progressBar;
	private ImageView ivCover;
	private CircleImageView cvProfile;
	private TextView tvUsername;
	private TextView tvTitle;
	private TextView tvLocation;
	private TextView tvDescription;
	private Button btShowProfile;
	private FloatingActionButton btAddComment;
	private RecyclerView rvComments;

	private String postUserId = "";
	private String extraPostId;

	private AuthProvider authProvider;
	private UsersProvider usersProvider;
	private PostsProvider postsProvider;
	private CommentsProvider commentsProvider;

	private CommentAdapter commentAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_post_detail);

		progressBar = findViewById(R.id.rl_progress);
		ivCover = findViewById(R.id.iv_cover);
		cvProfile = findViewById(R.id.civ_profile);
		tvUsername = findViewById(R.id.tv_username);
		tvTitle = findViewById(R.id.tv_title);
		tvLocation = findViewById(R.id.tv_location);
		tvDescription = findViewById(R.id.tv_description);
		btShowProfile = findViewById(R.id.bt_showProfile);
		btAddComment = findViewById(R.id.bt_addComment);
		rvComments = findViewById(R.id.rv_comments);

		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setTitle("");
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		extraPostId = getIntent().getStringExtra("documentId");

		authProvider = new AuthProvider();
		usersProvider = new UsersProvider();
		postsProvider = new PostsProvider();
		commentsProvider = new CommentsProvider();

		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostDetail.this);
		rvComments.setLayoutManager(linearLayoutManager);

		getPost();

		btAddComment.setOnClickListener(v -> showDialogComment());
		btShowProfile.setOnClickListener(v -> goToShowProfile());
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStart() {
		super.onStart();
		Query query = commentsProvider.getCommentsByPost(extraPostId).orderBy(Constants.COMMENT_CREATIONDATE, Query.Direction.ASCENDING);
		FirestoreRecyclerOptions<Comment> options = new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();
		commentAdapter = new CommentAdapter(options, PostDetail.this);
		rvComments.setAdapter(commentAdapter);
		commentAdapter.startListening();
	}

	@Override
	public void onStop() {
		super.onStop();
		commentAdapter.stopListening();
	}

	private void goToShowProfile() {
		if (!postUserId.equals(getString(R.string.empty))) {
			startActivity(new Intent(PostDetail.this, ProfileFromUser.class).putExtra("userId", postUserId));
		}
	}

	private void getPost() {
		postsProvider.getPostById(extraPostId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.POST_IMAGE)) {
					Glide.with(getApplicationContext()).load(documentSnapshot.getString(Constants.POST_IMAGE)).into(ivCover);
					ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
				}

				if (documentSnapshot.contains(Constants.POST_TITLE)) {
					tvTitle.setText(documentSnapshot.getString(Constants.POST_TITLE));
				}

				if (documentSnapshot.contains(Constants.POST_LOCATION)) {
					tvLocation.setText(documentSnapshot.getString(Constants.POST_LOCATION));
				}

				if (documentSnapshot.contains(Constants.POST_DESCRIPTION)) {
					tvDescription.setText(documentSnapshot.getString(Constants.POST_DESCRIPTION));
				}

				if (documentSnapshot.contains(Constants.POST_USERID)) {
					postUserId = documentSnapshot.getString(Constants.POST_USERID);
					getUserInfo(postUserId);
				}
			}
		});
	}

	private void getUserInfo(String userId) {
		usersProvider.getUser(userId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				if (documentSnapshot.contains(Constants.USER_IMAGEPROFILE)) {
					Glide.with(getApplicationContext()).load(documentSnapshot.getString(Constants.USER_IMAGEPROFILE)).circleCrop().into(cvProfile);
				}

				if (documentSnapshot.contains(Constants.USER_USERNAME)) {
					tvUsername.setText(documentSnapshot.getString(Constants.USER_USERNAME));
				}
			}
		});
	}

	private void showDialogComment() {
		AlertDialog.Builder alert = new AlertDialog.Builder(PostDetail.this);
		alert.setTitle(R.string.newComment);
		alert.setMessage(R.string.addYourComment);

		EditText editText = new EditText(PostDetail.this);
		editText.setInputType(InputType.TYPE_CLASS_TEXT);
		editText.setHint(R.string.addYourComment);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(36, 0, 36, 36);
		editText.setLayoutParams(params);

		RelativeLayout container = new RelativeLayout(PostDetail.this);
		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		container.setLayoutParams(relativeParams);
		container.addView(editText);

		alert.setView(container);

		alert.setPositiveButton("Add", (dialog, which) -> {
			showProgressBar();
			String commentText = editText.getText().toString().trim();
			if (Validations.validateEditTextIsEmpty(commentText)) {
				createComment(commentText);
			} else {
				hideProgressBar();
				Toast.makeText(PostDetail.this, getText(R.string.fieldCanNotEmpty), Toast.LENGTH_SHORT).show();
			}
		});

		alert.setNegativeButton("Cancel", (dialog, which) -> {
		});

		alert.show();
	}

	private void createComment(String commentText) {
		Comment comment = new Comment();
		comment.setText(commentText);
		comment.setPostId(extraPostId);
		comment.setUserId(authProvider.getUserId());
		comment.setCreationDate(new Date().getTime());
		commentsProvider.create(comment).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				hideProgressBar();
				Toast.makeText(PostDetail.this, getText(R.string.commentCreated), Toast.LENGTH_SHORT).show();
				PostUtils.sendNotification(this, authProvider.getUserId(), postUserId, Constants.COMMENTS);
			} else {
				hideProgressBar();
				Toast.makeText(PostDetail.this, getText(R.string.commentCouldNotBeCreated), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void showProgressBar() {
		progressBar.setVisibility(View.VISIBLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}

	private void hideProgressBar() {
		progressBar.setVisibility(View.GONE);
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
	}
}