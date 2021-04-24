package com.dcascos.motogo.layouts;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
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

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.models.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.PermissionUtils;
import com.dcascos.motogo.utils.Validations;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

	private RelativeLayout progressBar;
	private ImageView ivCover;
	private CircleImageView circleImageProfile;
	private TextInputLayout tiFullname;
	private TextInputLayout tiUsername;
	private TextInputLayout tiEmail;
	private ImageButton ibBack;
	private Button btUpdateProfile;
	private AlertDialog.Builder builderSelector;

	private byte[] imageFileCover;
	private byte[] imageFileProfile;

	private CharSequence[] dialogOptions;

	private static final int SELECT_PHOTO_COVER = 1;
	private static final int SELECT_PHOTO_PROFILE = 2;

	private AuthProvider authProvider;
	private UsersProvider usersProvider;
	private ImageProvider imageProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_edit_profile);

		progressBar = findViewById(R.id.rl_progress);
		ivCover = findViewById(R.id.iv_cover);
		circleImageProfile = findViewById(R.id.circleImageProfile);
		tiFullname = findViewById(R.id.ti_fullname);
		tiUsername = findViewById(R.id.ti_username);
		tiEmail = findViewById(R.id.ti_email);
		ibBack = findViewById(R.id.ib_back);
		btUpdateProfile = findViewById(R.id.bt_updateProfile);

		builderSelector = new AlertDialog.Builder(this);
		builderSelector.setTitle(R.string.selectAnOption);
		dialogOptions = new CharSequence[]{getString(R.string.takePhoto), getString(R.string.imageFromGallery)};

		authProvider = new AuthProvider();
		usersProvider = new UsersProvider();
		imageProvider = new ImageProvider();

		getUserData();

		ibBack.setOnClickListener(v -> this.onBackPressed());

		ivCover.setOnClickListener(v -> selectOptionImage(SELECT_PHOTO_COVER));
		circleImageProfile.setOnClickListener(v -> selectOptionImage(SELECT_PHOTO_PROFILE));

		btUpdateProfile.setOnClickListener(v -> getProfileData());
	}

	private void getUserData() {
		usersProvider.getUser(authProvider.getUserId()).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists()) {
				Objects.requireNonNull(tiFullname.getEditText()).setText(documentSnapshot.getString(Constants.USER_FULLNAME));
				Objects.requireNonNull(tiUsername.getEditText()).setText(documentSnapshot.getString(Constants.USER_USERNAME));
				Objects.requireNonNull(tiEmail.getEditText()).setText(documentSnapshot.getString(Constants.USER_EMAIL));
				Glide.with(this).load(documentSnapshot.getString(Constants.USER_IMAGECOVER)).into(ivCover);
				Glide.with(this).load(documentSnapshot.getString(Constants.USER_IMAGEPROFILE)).circleCrop().into(circleImageProfile);

				ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
		});
	}

	private void selectOptionImage(int selectedImage) {
		builderSelector.setItems(dialogOptions, (dialog, which) -> {
			if (which == 0) {
				if (!PermissionUtils.hasPermission(EditProfile.this, Manifest.permission.CAMERA)) {
					PermissionUtils.requestPermissions(EditProfile.this, new String[]{Manifest.permission.CAMERA}, Constants.REQUEST_CODE_PHOTO);
				} else {
					if (selectedImage == SELECT_PHOTO_COVER) {
						takePhoto(Constants.REQUEST_CODE_PHOTO_COVER);
					} else if (selectedImage == SELECT_PHOTO_PROFILE) {
						takePhoto(Constants.REQUEST_CODE_PHOTO_PROFILE);
					}
				}
			} else if (which == 1) {
				if (!PermissionUtils.hasPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
					PermissionUtils.requestPermissions(EditProfile.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, Constants.REQUEST_CODE_GALLERY);
				} else {
					if (selectedImage == SELECT_PHOTO_COVER) {
						openGallery(Constants.REQUEST_CODE_GALLERY_COVER);
					} else if (selectedImage == SELECT_PHOTO_PROFILE) {
						openGallery(Constants.REQUEST_CODE_GALLERY_PROFILE);
					}
				}
			}
		});
		builderSelector.show();
	}

	private void takePhoto(int requestCode) {
		Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(takePhotoIntent, requestCode);
	}

	private void openGallery(int requestCode) {
		Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
		startActivityForResult(galleryIntent, requestCode);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if ((requestCode == Constants.REQUEST_CODE_PHOTO_COVER && resultCode == RESULT_OK)
				|| (requestCode == Constants.REQUEST_CODE_PHOTO_PROFILE && resultCode == RESULT_OK)
				|| (requestCode == Constants.REQUEST_CODE_GALLERY_COVER && resultCode == RESULT_OK)
				|| (requestCode == Constants.REQUEST_CODE_GALLERY_PROFILE && resultCode == RESULT_OK)) {
			try {
				setImageFile(data, requestCode);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void setImageFile(Intent data, int requestCode) throws IOException {
		if (requestCode == Constants.REQUEST_CODE_PHOTO_COVER || requestCode == Constants.REQUEST_CODE_GALLERY_COVER) {
			Bitmap coverImage;
			if (requestCode == Constants.REQUEST_CODE_GALLERY_COVER) {
				coverImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
			} else {
				coverImage = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			coverImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			ivCover.setImageBitmap(coverImage);
			ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			imageFileCover = baos.toByteArray();
		}

		if (requestCode == Constants.REQUEST_CODE_PHOTO_PROFILE || requestCode == Constants.REQUEST_CODE_GALLERY_PROFILE) {
			Bitmap profileImage;
			if (requestCode == Constants.REQUEST_CODE_GALLERY_PROFILE) {
				profileImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
			} else {
				profileImage = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
			}
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			profileImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			circleImageProfile.setImageBitmap(profileImage);
			imageFileProfile = baos.toByteArray();
		}
	}

	private void getProfileData()  {
		if (Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
				& Validations.validateUsernameFormat(getApplicationContext(), tiUsername)) {

			showProgressBar();

			String fullname = Objects.requireNonNull(tiFullname.getEditText()).getText().toString().trim();
			String username = Objects.requireNonNull(tiUsername.getEditText()).getText().toString().trim();

			if (imageFileCover != null && imageFileProfile != null) {
				saveCoverAndProfileImages(imageFileCover, imageFileProfile, fullname, username);
			} else if (imageFileCover != null) {
				saveImage(imageFileCover, true, fullname, username);
			} else if (imageFileProfile != null) {
				saveImage(imageFileProfile, false, fullname, username);
			} else {
				updateProfile(fullname, username, null, null);
			}
		}
	}

	private void saveImage(byte[] imageFile, boolean isImageCover, String fullname, String username) {
		String folder;

		if (isImageCover) {
			folder = Constants.FOLDER_COVER;
		} else {
			folder = Constants.FOLDER_PROFILE;
		}
		imageProvider.saveFromBytes(imageFile, folder).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
					if (isImageCover) {
						updateProfile(fullname, username, uri.toString(), null);
					} else {
						updateProfile(fullname, username, null, uri.toString());
					}
				});
			} else {
				hideProgressBar();
				Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void saveCoverAndProfileImages(byte[] fileCover, byte[] fileProfile, String fullname, String username) {
		imageProvider.saveFromBytes(fileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
			if (task.isSuccessful()) {
				imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uriCover ->
						imageProvider.saveFromBytes(fileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task1 -> {
							if (task1.isSuccessful()) {
								imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uriProfile ->
										updateProfile(fullname, username, uriCover.toString(), uriProfile.toString()));
							} else {
								hideProgressBar();
								Toast.makeText(EditProfile.this, getText(R.string.profilePhotoNoUploaded), Toast.LENGTH_SHORT).show();
							}
						}));
			} else {
				hideProgressBar();
				Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void updateProfile(String fullname, String username, String urlCover, String urlProfile) {
		User user = new User();
		user.setId(authProvider.getUserId());
		user.setFullName(fullname);
		user.setUsername(username);
		user.setImageCover(urlCover);
		user.setImageProfile(urlProfile);

		usersProvider.update(user).addOnCompleteListener(task1 -> {
			if (task1.isSuccessful()) {
				hideProgressBar();
				finish();
				Toast.makeText(EditProfile.this, getText(R.string.theUserHasBeenUpdated), Toast.LENGTH_SHORT).show();
			} else {
				hideProgressBar();
				Toast.makeText(EditProfile.this, getText(R.string.userCouldNotBeUpdated), Toast.LENGTH_SHORT).show();
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