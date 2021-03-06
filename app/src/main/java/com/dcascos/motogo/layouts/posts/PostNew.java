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

import com.dcascos.motogo.utils.MainActivity;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.database.Post;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.database.PostsProvider;
import com.dcascos.motogo.utils.PermissionUtils;
import com.dcascos.motogo.utils.Validations;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class PostNew extends MainActivity {

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
	private PostsProvider postsProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_post_new);

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
		postsProvider = new PostsProvider();

		ivCover.setOnClickListener(v -> selectOptionImage());

		tiLocation.getEditText().setOnClickListener(v -> getAutocompletedLocation());

		btPublish.setOnClickListener(v -> createPost());

		ibBack.setOnClickListener(v -> this.onBackPressed());
	}

	private void getAutocompletedLocation() {
		if (!Places.isInitialized()) {
			Places.initialize(this, getResources().getString(R.string.google_api_key));
		}
		List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS);

		Intent autoCompleteIntent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(this);
		startActivityForResult(autoCompleteIntent, Constants.REQUEST_CODE_AUTOCOMPLETE);
	}

	private void selectOptionImage() {
		builderSelector.setItems(dialogOptions, (dialog, which) -> {
			if (which == 0) {
				if (!PermissionUtils.hasPermission(PostNew.this, Manifest.permission.CAMERA)) {
					if (PermissionUtils.shouldShowRational(PostNew.this, Manifest.permission.CAMERA)) {
						Toast.makeText(PostNew.this, getText(R.string.permissionCamera), Toast.LENGTH_LONG).show();
					}
					PermissionUtils.requestPermissions(PostNew.this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PHOTO);
				} else {
					takePhoto();
				}
			} else if (which == 1) {
				if (!PermissionUtils.hasPermission(PostNew.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					if (PermissionUtils.shouldShowRational(PostNew.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
						Toast.makeText(PostNew.this, getText(R.string.permissionStorage), Toast.LENGTH_LONG).show();
					}
					PermissionUtils.requestPermissions(PostNew.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_GALLERY);
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
					coverImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Objects.requireNonNull(data).getData());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				coverImage = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			Objects.requireNonNull(coverImage).compress(Bitmap.CompressFormat.JPEG, 100, baos);
			ivCover.setImageBitmap(coverImage);
			ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			photoFile = baos.toByteArray();
		}

		if (requestCode == Constants.REQUEST_CODE_AUTOCOMPLETE && resultCode == RESULT_OK) {
			Place place = Autocomplete.getPlaceFromIntent(data);
			tiLocation.getEditText().setText(place.getAddress());
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
						Toast.makeText(PostNew.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
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

		postsProvider.create(post).addOnCompleteListener(task1 -> {
			if (task1.isSuccessful()) {
				finish();
				Toast.makeText(PostNew.this, getText(R.string.postCreated), Toast.LENGTH_SHORT).show();
			} else {
				hideProgressBar();
				Toast.makeText(PostNew.this, getText(R.string.postNoUploaded), Toast.LENGTH_SHORT).show();
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