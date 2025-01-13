package com.example.realestatehub.LogIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton, continueButton;
    private TextView pageNameTextView, mainTextView;
    private ImageView forgotPasswordImageView, loadingImageView;
    private CardView emailCardView;
    private EditText emailEditText;
    private final int delay = 4000;
    private final Handler handler = new Handler();
    private Intent intent;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.activity1_forgot_password);
        initUI();
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);
        pageNameTextView = findViewById(R.id.pageNameTextView);
        forgotPasswordImageView = findViewById(R.id.forgotPasswordImageView);
        mainTextView = findViewById(R.id.mainTextView);
        emailCardView = findViewById(R.id.email_cardview);
        emailEditText = findViewById(R.id.emailEditText);
        loadingImageView = findViewById(R.id.dialog_loading);

        backButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        database = Database.getInstance(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(this, ConnectingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.backButton) {
            onBackPressed();
        } else if (id == R.id.continueButton) {
            //Check if email is valid and send password reset link
            //Check if email is valid -> Changing Visibilities + Opening ConnectingActivity
            emailChecker();
        }
    }

    private void emailChecker() {
        final String email = emailEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please re-enter your Email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Valid Email is required");
            emailEditText.requestFocus();
        } else {
            database.sendPasswordRestLink(email, new Database.GeneralCallback() {

                @Override
                public void onSuccess() {
                    changeVisibilities();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(ForgotPasswordActivity.this, ConnectingActivity.class));
                            finish();
                        }
                    }, delay);
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    if (errorCode == 1) {
                        emailEditText.setError(errorMessage);
                        emailEditText.requestFocus();
                    } else if (errorCode == 2) {
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else if (errorCode == 3) {
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else if (errorCode == 4) {
                        emailEditText.setError(errorMessage);
                        emailEditText.requestFocus();
                        Toast.makeText(ForgotPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void changeVisibilities() {
        backButton.setVisibility(View.INVISIBLE);
        pageNameTextView.setVisibility(View.INVISIBLE);
        forgotPasswordImageView.setVisibility(View.INVISIBLE);
        mainTextView.setText("Successful!\nPlease check your Email in order to reset your password.");
        emailCardView.setVisibility(View.INVISIBLE);
        continueButton.setVisibility(View.INVISIBLE);
        Glide.with(this).asGif().load(R.drawable.loading2).into(loadingImageView);
        loadingImageView.setVisibility(View.VISIBLE);
    }
}
