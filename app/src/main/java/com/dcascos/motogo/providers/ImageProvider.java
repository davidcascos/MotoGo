package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.utils.Generators;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageProvider {

	StorageReference storageReference;

	public ImageProvider() {
		storageReference = FirebaseStorage.getInstance().getReference();
	}

	public StorageReference getStorage() {
		return storageReference;
	}

	public UploadTask save(File file, String folder) throws FileNotFoundException {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + folder + "/" + Generators.photoNameFormater() + ".jpeg");
		InputStream inputStream = new FileInputStream(file);
		this.storageReference = storageReference;
		return storageReference.putStream(inputStream);
	}

	public UploadTask saveFromBytes(byte[] byteFile, String folder) {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + folder + "/" + Generators.photoNameFormater() + ".jpeg");
		this.storageReference = storageReference;
		return storageReference.putBytes(byteFile);
	}

	public StorageReference saveCoverWithoutImage() {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + Constants.FOLDER_COVER + "/defaultCover.jpeg");
		this.storageReference = storageReference;
		return storageReference;
	}

	public StorageReference saveProfileWithoutImage() {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("images/" + Constants.FOLDER_PROFILE + "/defaultProfile.jpeg");
		this.storageReference = storageReference;
		return storageReference;
	}

}
