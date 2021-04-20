package com.dcascos.motogo.layouts;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.dcascos.motogo.layouts.loginSignup.Login;
import com.dcascos.motogo.R;

public class SplashScreen extends AppCompatActivity {

	private static final int SPLASH_SCREEN_TIME = 3000;

	private ImageView ivLogo;
	private TextView tvTitle;

	@Override
	@SuppressWarnings("DEPRECATION")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ac_splash_screen);

		checkVersion();
		doAnimations();
		timerSplashScreen();
	}

	private void checkVersion() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
			final WindowInsetsController insetsController = getWindow().getInsetsController();
			if (insetsController != null) {
				insetsController.hide(WindowInsets.Type.statusBars());
			}
		} else {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}

	private void doAnimations() {
		Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
		Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

		ivLogo = findViewById(R.id.iv_logo);
		tvTitle = findViewById(R.id.tv_title);

		ivLogo.setAnimation(topAnim);
		tvTitle.setAnimation(bottomAnim);
	}

	private void timerSplashScreen() {
		new Handler(Looper.getMainLooper()).postDelayed(() -> {

			Pair[] pairs = new Pair[2];
			pairs[0] = new Pair<View, String>(ivLogo, "tran_logo");
			pairs[1] = new Pair<View, String>(tvTitle, "tran_title");

			ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this, pairs);

			startActivity(new Intent(SplashScreen.this, Login.class), options.toBundle());
			finish();
		}, SPLASH_SCREEN_TIME);
	}
}