package com.example.realestatehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ForgotPasswordActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private TextView pageNameTextView;
    private ImageView forgotPasswordImageView;
    private TextView mainTextView;
    private CardView emailCardView;
    private EditText emailEditText;
    private Button continueButton;
    private ImageView loadingImageView;
    private FirebaseAuth auth;
    private final int delay = 4000;
    private Handler handler = new Handler();
    private Intent intent;
    private final static String TAG = "ForgotPasswordActivity";

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
        pageNameTextView = findViewById(R.id.pageNameTextView);
        forgotPasswordImageView = findViewById(R.id.forgotPasswordImageView);
        mainTextView = findViewById(R.id.mainTextView);
        emailCardView = findViewById(R.id.email_cardview);
        emailEditText = findViewById(R.id.emailEditText);
        continueButton = findViewById(R.id.continueButton);
        loadingImageView = findViewById(R.id.dialog_loading);

        backButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
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
            startActivity(new Intent(ForgotPasswordActivity.this, ConnectingActivity.class));
            finish();
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
            DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("Registered Users");
            usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    boolean isValidEmail = false;
                    for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                        String userEmail = userSnapshot.child("email").getValue(String.class);
                        if (userEmail != null && userEmail.equals(email)) {
                            isValidEmail = true;
                            break;
                        }
                    }
                    if (isValidEmail) {
                        // Email is valid, continue with password reset
                        auth = FirebaseAuth.getInstance();
                        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    changeVisibilities();
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(ForgotPasswordActivity.this, ConnectingActivity.class));
                                            finish();
                                        }
                                    }, delay);
                                } else {
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthInvalidUserException e) {
                                        emailEditText.setError("User doesn't exist.\nPlease register again.");
                                    } catch (Exception e) {
                                        Log.e(TAG, e.getMessage());
                                        Toast.makeText(ForgotPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    Toast.makeText(ForgotPasswordActivity.this, "Failed to send Email", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        // Email is not in our Database
                        emailEditText.setError("Email doesn't exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
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
