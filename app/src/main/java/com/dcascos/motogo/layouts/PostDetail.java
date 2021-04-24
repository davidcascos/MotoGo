package com.dcascos.motogo.layouts;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.dcascos.motogo.R;
import com.dcascos.motogo.constants.Constants;
import com.dcascos.motogo.providers.PostProvider;

public class PostDetail extends AppCompatActivity {

	private ImageView ivCover;

	private String extraPostId;

	PostProvider postProvider;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_post_detail);

		ivCover = findViewById(R.id.iv_cover);

		extraPostId = getIntent().getStringExtra("documentId");

		postProvider = new PostProvider();

		getPost();
	}

	private void getPost() {
		postProvider.getPostById(extraPostId).addOnSuccessListener(documentSnapshot -> {
			if (documentSnapshot.exists() && documentSnapshot.contains(Constants.POST_IMAGE)) {
				Glide.with(getApplicationContext()).load(documentSnapshot.getString(Constants.POST_IMAGE)).into(ivCover);
				ivCover.setScaleType(ImageView.ScaleType.CENTER_CROP);
			}
		});
	}
}