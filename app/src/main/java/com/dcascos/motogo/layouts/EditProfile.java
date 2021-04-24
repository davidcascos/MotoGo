package com.dcascos.motogo.layouts;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.dcascos.motogo.models.User;
import com.dcascos.motogo.providers.AuthProvider;
import com.dcascos.motogo.providers.ImageProvider;
import com.dcascos.motogo.providers.UsersProvider;
import com.dcascos.motogo.utils.FileUtils;
import com.dcascos.motogo.utils.PermissionUtils;
import com.dcascos.motogo.utils.Validations;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
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

	private File imageFileCover;
	private byte[] photoFileCover;
	private File imageFileProfile;
	private byte[] photoFileProfile;
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

		ibBack.setOnClickListener(v -> this.onBackPressed());

		ivCover.setOnClickListener(v -> selectOptionImage(SELECT_PHOTO_COVER));
		circleImageProfile.setOnClickListener(v -> selectOptionImage(SELECT_PHOTO_PROFILE));

		btUpdateProfile.setOnClickListener(v -> {
			try {
				getProfileData();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
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
		//Take photo
		if (requestCode == Constants.REQUEST_CODE_PHOTO_COVER && resultCode == RESULT_OK) {
			setPhotoFile(data, requestCode);
		}
		if (requestCode == Constants.REQUEST_CODE_PHOTO_PROFILE && resultCode == RESULT_OK) {
			setPhotoFile(data, requestCode);
		}

		//Select from gallery
		if (requestCode == Constants.REQUEST_CODE_GALLERY_COVER && resultCode == RESULT_OK) {
			setGalleryFile(data, requestCode);
		}
		if (requestCode == Constants.REQUEST_CODE_GALLERY_PROFILE && resultCode == RESULT_OK) {
			setGalleryFile(data, requestCode);
		}
	}

	private void setPhotoFile(Intent data, int requestCode) {
		Bitmap photo = (Bitmap) Objects.requireNonNull(data).getExtras().get("data");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);

		if (requestCode == Constants.REQUEST_CODE_PHOTO_COVER) {
			ivCover.setImageBitmap(photo);
			ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			photoFileCover = baos.toByteArray();
		} else if (requestCode == Constants.REQUEST_CODE_PHOTO_PROFILE) {
			circleImageProfile.setImageBitmap(photo);
			circleImageProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
			photoFileProfile = baos.toByteArray();
		}
	}

	private void setGalleryFile(Intent data, int requestCode) {
		if (requestCode == Constants.REQUEST_CODE_GALLERY_COVER) {
			try {
				imageFileCover = FileUtils.from(this, Objects.requireNonNull(data).getData());
				ivCover.setImageBitmap(BitmapFactory.decodeFile(imageFileCover.getAbsolutePath()));
				ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else if (requestCode == Constants.REQUEST_CODE_GALLERY_PROFILE) {
			try {
				imageFileProfile = FileUtils.from(this, Objects.requireNonNull(data).getData());
				circleImageProfile.setImageBitmap(BitmapFactory.decodeFile(imageFileProfile.getAbsolutePath()));
				circleImageProfile.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void getProfileData() throws FileNotFoundException {
		if (Validations.validateFullNameFormat(getApplicationContext(), tiFullname)
				& Validations.validateUsernameFormat(getApplicationContext(), tiUsername)) {

			showProgressBar();

			String fullname = Objects.requireNonNull(tiFullname.getEditText()).getText().toString().trim();
			String username = Objects.requireNonNull(tiUsername.getEditText()).getText().toString().trim();


			if (photoFileCover != null && photoFileProfile != null) {
				imageProvider.saveFromBytes(photoFileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri ->
								imageProvider.saveFromBytes(photoFileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task1 -> saveImageProfile(task1, uri, fullname, username)));
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (imageFileCover != null && photoFileProfile != null) {
				imageProvider.save(imageFileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri ->
								imageProvider.saveFromBytes(photoFileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task1 -> saveImageProfile(task1, uri, fullname, username)));
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (photoFileCover != null && imageFileProfile != null) {
				imageProvider.saveFromBytes(photoFileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
							try {
								imageProvider.save(imageFileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task1 -> saveImageProfile(task1, uri, fullname, username));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						});
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (imageFileCover != null && imageFileProfile != null) {
				imageProvider.save(imageFileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
							try {
								imageProvider.save(imageFileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task1 -> saveImageProfile(task1, uri, fullname, username));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}
						});
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (photoFileCover != null) {
				imageProvider.saveFromBytes(photoFileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> updateProfile(uri.toString(), null, fullname, username));
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (photoFileProfile != null) {
				imageProvider.saveFromBytes(photoFileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> updateProfile(null, uri.toString(), fullname, username));
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.profilePhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (imageFileCover != null) {
				imageProvider.save(imageFileCover, Constants.FOLDER_COVER).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> updateProfile(uri.toString(), null, fullname, username));
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.coverPhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else if (imageFileProfile != null) {
				imageProvider.save(imageFileProfile, Constants.FOLDER_PROFILE).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(uri -> updateProfile(null, uri.toString(), fullname, username));
					} else {
						hideProgressBar();
						Toast.makeText(EditProfile.this, getText(R.string.profilePhotoNoUploaded), Toast.LENGTH_SHORT).show();
					}
				});
			} else {
				updateProfile(null, null, fullname, username);
			}
		}
	}

	private void saveImageProfile(Task task, Uri urlCover, String fullname, String username) {
		if (task.isSuccessful()) {
			imageProvider.getStorage().getDownloadUrl().addOnSuccessListener(urlProfile -> updateProfile(urlCover.toString(), urlProfile.toString(), fullname, username));
		} else {
			hideProgressBar();
			Toast.makeText(EditProfile.this, getText(R.string.profilePhotoNoUploaded), Toast.LENGTH_SHORT).show();
		}
	}


	private void updateProfile(String urlCover, String urlProfile, String fullname, String username) {
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