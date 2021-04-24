package com.dcascos.motogo.providers;

import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.utils.Generators;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class ImageProvider {

	StorageReference storageReference;

	public ImageProvider() {
		storageReference = FirebaseStorage.getInstance().getReference();
	}

	public StorageReference getStorage() {
		return storageReference;
	}

	public UploadTask saveFromBytes(byte[] byteFile, String folder) {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Constants.FOLDER_IMAGES + "/" + folder + "/" + Generators.photoNameFormater() + ".jpeg");
		this.storageReference = storageReference;
		return storageReference.putBytes(byteFile);
	}

	public StorageReference saveCoverWithoutImage() {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Constants.FOLDER_IMAGES + "/" + Constants.FOLDER_COVER + "/defaultCover.jpeg");
		this.storageReference = storageReference;
		return storageReference;
	}

	public StorageReference saveProfileWithoutImage() {
		StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(Constants.FOLDER_IMAGES + "/" + Constants.FOLDER_PROFILE + "/defaultProfile.jpeg");
		this.storageReference = storageReference;
		return storageReference;
	}

}
