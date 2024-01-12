package com.example.realestatehub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.Firebase;

public class LoadingActivity extends AppCompatActivity {
    private ImageView loadingImageView;
    private final int delay = 2000;
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

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Start the next activity or move to the next layout here
                startActivity(new Intent(LoadingActivity.this, logInActivity.class));
                finish();
            }
        }, delay);
    }

    private void initUI() {
        loadingImageView = findViewById(R.id.loadingBarImageView);
        // Load and animate the GIF using Glide
        Glide.with(this).asGif().load(R.drawable.loading2).into(loadingImageView);
    }

}
