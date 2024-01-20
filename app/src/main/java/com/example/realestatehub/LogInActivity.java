package com.example.realestatehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signInButton;
    private TextView logInTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        setContentView(R.layout.activity_connecting);
        initUI();
    }

    private void initUI() {
        signInButton = findViewById(R.id.signInButton);
        logInTextView = findViewById(R.id.logInTextView);
        signInButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signInButton) {
            Intent intent = new Intent(this, SetIntentActivity.class);
            startActivity(intent);
            finish();
        }
    }
}