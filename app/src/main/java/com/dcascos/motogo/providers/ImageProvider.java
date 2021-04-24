package com.dcascos.motogo.providers;

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

	public UploadTask save(File file) throws FileNotFoundException {
		StorageReference storageReference = this.storageReference.child("images/" + Generators.photoNameFormater() + ".jpeg");
		InputStream inputStream = new FileInputStream(file);

		this.storageReference = storageReference;
		return storageReference.putStream(inputStream);
	}

	public UploadTask saveFromBytes(byte[] byteFile) {
		StorageReference storageReference = this.storageReference.child("images/" + Generators.photoNameFormater() + ".jpeg");

		this.storageReference = storageReference;
		return storageReference.putBytes(byteFile);
	}

	public StorageReference saveWithoutImage() {
		StorageReference storageReference = this.storageReference.child("images/defaultCover.jpeg");
		this.storageReference = storageReference;
		return storageReference;
	}

}
