package com.example.realestatehub.UserData;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.Utils.Database;
import com.example.realestatehub.Utils.Language;
import com.example.realestatehub.LogIn.ConnectingActivity;
import com.example.realestatehub.R;
import com.google.firebase.auth.FirebaseUser;

public class LoadingActivity extends AppCompatActivity {
    private ImageView loadingImageView;
    private final int delay = 2500;
    private Handler handler = new Handler();

    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.ic_launcher_background));

        Language.loadLocale(this);

        setContentView(R.layout.activity_loading);
        initUI();

    }

    private void initUI() {
        loadingImageView = findViewById(R.id.loadingBarImageView);
        // Load and animate the GIF using Glide
        Glide.with(this).asGif().load(R.drawable.loading2).into(loadingImageView);

        database = new Database(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                FirebaseUser firebaseUser = database.getFirebaseUser();
                if (firebaseUser != null && firebaseUser.isEmailVerified()) {
                    Intent intent = new Intent(LoadingActivity.this, HomeBottomNavigation.class);
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
