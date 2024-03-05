package com.example.realestatehub.LogIn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.HomeFragments.UploadPost.AddPostActivity;
import com.example.realestatehub.Utils.Database;
import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.HashMap;

import javax.annotation.Nullable;

public class ConnectingActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailEditText, passwordEditText;
    private Button signInButton, forgotPasswordButton, facebookButton, googleButton, microsoftButton, signUpButton;
    private Intent intent;
    private GoogleSignInClient GoogleSignInClient;
    int RC_SIGN_IN = 20;
    private boolean fromGoogle = false;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_connecting);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));

        initUI();
    }

    private void initUI() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        GoogleSignInClient = GoogleSignIn.getClient(this, gso);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
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

        database = new Database(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.signInButton) {
            regularLogIn();
        } else if (id == R.id.forgotPasswordButton) {
            intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.googleButton) {
            fromGoogle = googleSignIn();
        } else if (id == R.id.signUpButton) {
            intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void regularLogIn() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please enter your Email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please re-enter your Email", Toast.LENGTH_SHORT).show();
            emailEditText.setError("Valid Email is required");
            emailEditText.requestFocus();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
        } else {
            database.login(email, password, new Database.RegistrationCallback() {

                @Override
                public void onSuccess() {
                    intent = new Intent(ConnectingActivity.this, HomeBottomNavigation.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    if (errorCode == 0) {
                        Toast.makeText(ConnectingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else if (errorCode == 1) {
                        emailEditText.setError(errorMessage);
                        emailEditText.requestFocus();
                    } else if (errorCode == 2) {
                        passwordEditText.setError(errorMessage);
                        passwordEditText.requestFocus();
                    } else if (errorCode == 3) {
                        Toast.makeText(ConnectingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        showAlertDialog();
                    }
                }
            });
        }
    }

    private boolean googleSignIn() {
        Intent signInIntent = GoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                database.firebaseAuthWithGoogle(account.getIdToken(), new Database.GoogleRegistrationCallback() {

                    @Override
                    public void onSuccess(boolean newUser, HashMap<String, Object> map) {
                        if (newUser) {
                            intent = new Intent(ConnectingActivity.this, SignUpActivity.class);
                            intent.putExtra("userData", map);
                            intent.putExtra("fromGoogle", fromGoogle);
                        } else {
                            intent = new Intent(ConnectingActivity.this, HomeBottomNavigation.class);
                        }
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(String errorMessage) {
                        Toast.makeText(ConnectingActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email Verification");
        builder.setMessage("Please check your email and verify your account in order to log in.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}