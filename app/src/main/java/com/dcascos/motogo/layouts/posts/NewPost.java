package com.dcascos.motogo.layouts.posts;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.Post;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.PostProvider;
import com.dcascos.motogo.utils.PermissionUtils;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;

public class NewPost extends AppCompatActivity {

	private RelativeLayout progressBar;
	private ImageView ivCover;
	private TextInputLayout tiTitle;
	private TextInputLayout tiLocation;
	private TextInputLayout tiDescription;
	private Button btPublish;
	private ImageButton ibBack;
	private AlertDialog.Builder builderSelector;

	private byte[] photoFile;
	private CharSequence[] dialogOptions;

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

		builderSelector = new AlertDialog.Builder(this);
		builderSelector.setTitle(R.string.selectAnOption);
		dialogOptions = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.imageFromGallery)};

		authProvider = new AuthProvider();
		imageProvider = new ImageProvider();
		postProvider = new PostProvider();

		ivCover.setOnClickListener(v -> selectOptionImage());

		btPublish.setOnClickListener(v -> createPost());

		ibBack.setOnClickListener(v -> this.onBackPressed());
	}

	private void selectOptionImage() {
		builderSelector.setItems(dialogOptions, (dialog, which) -> {
			if (which == 0) {
				if (!PermissionUtils.hasPermission(NewPost.this, Manifest.permission.CAMERA)) {
					PermissionUtils.requestPermissions(NewPost.this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PHOTO);
				} else {
					takePhoto();
				}
			} else if (which == 1) {
				if (!PermissionUtils.hasPermission(NewPost.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					PermissionUtils.requestPermissions(NewPost.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_GALLERY);
				} else {
					openGallery();
				}
			}
		});
		builderSelector.show();
	}

	private void takePhoto() {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePhotoIntent, Constants.REQUEST_CODE_PHOTO);
	}

	private void openGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
		startActivityForResult(galleryIntent, Constants.REQUEST_CODE_GALLERY);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == Constants.REQUEST_CODE_PHOTO && resultCode == RESULT_OK
				|| requestCode == Constants.REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {

			Bitmap coverImage = null;
			if (requestCode == Constants.REQUEST_CODE_GALLERY) {
				try {
					coverImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				coverImage = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			coverImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			ivCover.setImageBitmap(coverImage);
			ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			photoFile = baos.toByteArray();
		}
	}

	private void createPost() {
		if (Validations.validateIsEmpty(getApplicationContext(), tiTitle)
				& Validations.validateIsEmpty(getApplicationContext(), tiLocation)
				& Validations.validateIsEmpty(getApplicationContext(), tiDescription)) {

			showProgressBar();

			String title = Objects.requireNonNull(tiTitle.getEditText()).getText().toString().trim();
			String location = Objects.requireNonNull(tiLocation.getEditText()).getText().toString().trim();
			String description = Objects.requireNonNull(tiDescription.getEditText()).getText().toString().trim();

			if (photoFile != null) {
				imageProvider.saveFromBytes(photoFile, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> savePost(uri, title, location, description));
					} else {
						hideProgressBar();
						Toast.makeText(NewPost.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				imageProvider.saveCoverWithoutImage().getDownloadUrl().addOnSuccessListener(uri -> savePost(uri, title, location, description));
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
		post.setCreationDate(new Date().getTime());

		postProvider.create(post).addOnCompleteListener(task1 -> {
			if (task1.isSuccessful()) {
				finish();
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