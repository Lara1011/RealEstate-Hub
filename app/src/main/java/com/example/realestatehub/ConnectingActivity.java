package com.example.realestatehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nullable;

public class ConnectingActivity extends AppCompatActivity implements View.OnClickListener {
    private Button signInButton;
    private Button forgotPasswordButton;
    private Button facebookButton;
    private Button googleButton;
    private Button microsoftButton;
    private Button signUpButton;
    private Intent intent;
    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private GoogleSignInClient GoogleSignInClient;
    int RC_SIGN_IN = 20;

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
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient = GoogleSignIn.getClient(this, gso);

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
        int id = v.getId();
        if (id == R.id.signInButton) {
            regularSignIn();
        }
        else if (id == R.id.forgotPasswordButton) {
            intent = new Intent(this, ForgotPasswordActivity.class);
            startActivity(intent);
            finish();
        }
        else if (id == R.id.googleButton) {
            googleSignIn();
        }
        else if (id == R.id.signUpButton) {
            intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void regularSignIn() {

    }

    private void googleSignIn() {
        Intent signInIntent = GoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    short epsTime = 1000;
                    long dateCreate = Objects.requireNonNull(user.getMetadata()).getCreationTimestamp();
                    long dateLastLogin = user.getMetadata().getLastSignInTimestamp();
                    long currTime = System.currentTimeMillis();
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("id", user.getUid());
                    map.put("username", user.getDisplayName());
                    map.put("profilePic", user.getPhotoUrl().toString());
                    map.put("email", user.getEmail());
                    map.put("phoneNumber", user.getPhoneNumber());
                    map.put("created", dateCreate);
                    map.put("lastLogin", dateLastLogin);

                    database.getReference().child("Registered Users").child(user.getUid()).setValue(map);
                    Intent intent;
                    if (currTime < dateCreate + epsTime) {
                        intent = new Intent(ConnectingActivity.this, SignUpActivity.class);
                    } else {
                        intent = new Intent(ConnectingActivity.this, HomePageActivity.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ConnectingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}