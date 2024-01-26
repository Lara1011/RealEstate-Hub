package com.example.realestatehub;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView welcome_popup_ImageView;
    private ImageView welcome_finish_ImageView;
    private TextView welcome_text;
    private TextView welcome_info_text;
    private Button welcome_button;
    private Handler handler = new Handler();
    private final int delay = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        setContentView(R.layout.activity_welcome);
        initUI();
    }

    private void initUI() {
        welcome_popup_ImageView = findViewById(R.id.welcome_popup_ImageView);
        welcome_finish_ImageView = findViewById(R.id.welcome_finish_ImageView);
        welcome_text = findViewById(R.id.welcomeTextView);
        welcome_info_text = findViewById(R.id.welcomeInfoTextView);
        welcome_button = findViewById(R.id.welcomeButton);
        welcome_button.setOnClickListener(this);

        Glide.with(this).asGif().load(R.drawable.welcome_popup).into(welcome_popup_ImageView);

        animateTextView(welcome_text, 1000);
        animateTextView(welcome_info_text, 1000);
        animateTextView(welcome_button, 2000);
    }

    private void animateTextView(TextView textView, long milliseconds) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f);
        animator.setDuration(milliseconds).start();
    }

    private void handleGifs() {
        welcome_popup_ImageView.setVisibility(View.INVISIBLE);
        welcome_finish_ImageView.setVisibility(View.VISIBLE);
        Glide.with(WelcomeActivity.this).asGif().load(R.drawable.welcome_finish).into(welcome_finish_ImageView);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.welcomeButton) {
            handleGifs();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Start the next activity or move to the next layout here
                    startActivity(new Intent(WelcomeActivity.this, ConnectingActivity.class));
                    finish();
                }
            }, delay);
        }
    }
}
