package com.example.realestatehub;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class SetIntentActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private ImageView makingDealImageView;
    private Button continueButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        setContentView(R.layout.activity1_set_intent);
        initUI();
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        makingDealImageView = findViewById(R.id.makingDealImageView);
        continueButton = findViewById(R.id.continueButton);

        backButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        Glide.with(this).asGif().load(R.drawable.making_deal).into(makingDealImageView);
    }
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueButton) {

            // Start the next activity or move to the next layout here
            //startActivity(new Intent(WelcomeActivity.this, LogInActivity.class));
            finish();
        }
    }
}