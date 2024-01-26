package com.example.realestatehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ConnectingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signInButton;
    private Button forgotPasswordButton;
    private Button facebookButton;
    private Button googleButton;
    private Button microsoftButton;
    private Button signUpButton;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connecting);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        initUI();
    }

    private void initUI() {
        signInButton = findViewById(R.id.signInButton);
        forgotPasswordButton = findViewById(R.id.forgotPasswordButton);
        facebookButton = findViewById(R.id.facebookButton);
        googleButton = findViewById(R.id.googleButton);
        microsoftButton = findViewById(R.id.microsoftButton);
        signUpButton = findViewById(R.id.signUpButton);

        signInButton.setOnClickListener(this);
        forgotPasswordButton.setOnClickListener(this);
        facebookButton.setOnClickListener(this);
        googleButton.setOnClickListener(this);
        microsoftButton.setOnClickListener(this);
        signUpButton.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        //            case R.id.signInButton:
        //                break;
        //            case R.id.forgotPasswordButton:
        //                intent = new Intent(this, ForgotPasswordActivity.class);
        //                startActivity(intent);
        //                finish();
        //                break;
        //            case R.id.facebookButton:
        //                break;
        //            case R.id.googleButton:
        //                break;
        //            case R.id.microsoftButton:
        //                break;
        if (v.getId() == R.id.signUpButton) {
            intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }
}