package com.dcascos.motogo.layouts;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Post;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.PostProvider;
import com.dcascos.motogo.utils.FileUtil;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;

public class NewPost extends AppCompatActivity {

	private RelativeLayout progressBar;
	private ImageView ivCover;
	private TextInputLayout tiTitle;
	private TextInputLayout tiLocation;
	private TextInputLayout tiDescription;
	private Button btPublish;
	private ImageButton ibBack;

	private File imageFile;

	private AuthProvider authProvider;
	private ImageProvider imageProvider;
	private PostProvider postProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_new_post);

		progressBar = findViewById(R.id.rl_progress);
		ivCover = findViewById(R.id.iv_cover);
		tiTitle = findViewById(R.id.ti_title);
		tiLocation = findViewById(R.id.ti_location);
		tiDescription = findViewById(R.id.ti_description);
		btPublish = findViewById(R.id.bt_publish);
		ibBack = findViewById(R.id.ib_back);

		authProvider = new AuthProvider();
		imageProvider = new ImageProvider();
		postProvider = new PostProvider();

		ivCover.setOnClickListener(v -> openGallery());

		btPublish.setOnClickListener(v -> {
			try {
				createPost();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		});

		ibBack.setOnClickListener(v -> this.onBackPressed());
	}

	private void openGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
		galleryIntent.setType("image/*");
		startActivityForResult(galleryIntent, Constants.REQUEST_CODE_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
			try {
				imageFile = FileUtil.from(this, Objects.requireNonNull(data).getData());
				ivCover.setImageBitmap(BitmapFactory.decodeFile(imageFile.getAbsolutePath()));
				ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void createPost() throws FileNotFoundException {
		if (Validations.validateIsEmpty(getApplicationContext(), tiTitle)
				& Validations.validateIsEmpty(getApplicationContext(), tiLocation)
				& Validations.validateIsEmpty(getApplicationContext(), tiDescription)) {

			showProgressBar();

			String title = Objects.requireNonNull(tiTitle.getEditText()).getText().toString().trim();
			String location = Objects.requireNonNull(tiLocation.getEditText()).getText().toString().trim();
			String description = Objects.requireNonNull(tiDescription.getEditText()).getText().toString().trim();

			if (imageFile != null) {
				imageProvider.save(imageFile).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> savePost(uri, title, location, description));
					} else {
						hideProgressBar();
						Toast.makeText(NewPost.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				imageProvider.saveWithoutImage().getDownloadUrl().addOnSuccessListener(uri -> savePost(uri, title, location, description));
			}
		}
	}

	private void savePost(Uri uri, String title, String location, String description) {
		String url = uri.toString();
		Post post = new Post();
		post.setImage(url);
		post.setTitle(title);
		post.setLocation(location);
		post.setDescription(description);
		post.setUserId(authProvider.getUserId());

		postProvider.save(post).addOnCompleteListener(task1 -> {
			if (task1.isSuccessful()) {
				this.onBackPressed();
				Toast.makeText(NewPost.this, getText(R.string.postCreated), Toast.LENGTH_SHORT).show();
			} else {
				hideProgressBar();
				Toast.makeText(NewPost.this, getText(R.string.postNoUploaded), Toast.LENGTH_SHORT).show();
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