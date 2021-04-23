package com.dcascos.motogo.providers;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageProvider {

	StorageReference storageReference;

	public ImageProvider() {
		storageReference = FirebaseStorage.getInstance().getReference();
	}

	public StorageReference getStorage() {
		return storageReference;
	}

	public UploadTask save(File file) throws FileNotFoundException {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		String date = simpleDateFormat.format(new Date());

		StorageReference storageReference = this.storageReference.child("images/" + date + ".jpg");
		InputStream inputStream = new FileInputStream(file);

		this.storageReference = storageReference;
		return storageReference.putStream(inputStream);
	}

	public StorageReference saveWithoutImage() {
		StorageReference storageReference = this.storageReference.child("images/defaultCover.jpg");
		this.storageReference = storageReference;
		return storageReference;
	}

}
