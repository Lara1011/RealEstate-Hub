package com.example.realestatehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoadingActivity extends AppCompatActivity {
    private ImageView loadingImageView;
    private final int delay = 2500;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.ic_launcher_background));

        setContentView(R.layout.activity_loading);
        initUI();

    }

    private void initUI() {
        loadingImageView = findViewById(R.id.loadingBarImageView);
        // Load and animate the GIF using Glide
        Glide.with(this).asGif().load(R.drawable.loading2).into(loadingImageView);
    }

    @Override
    protected void onStart() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = auth.getCurrentUser();
                if (auth.getCurrentUser() != null && user.isEmailVerified()) {
                    Intent intent = new Intent(LoadingActivity.this, HomePageActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LoadingActivity.this, ConnectingActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, delay);

    }
}
