package com.example.realestatehub.LogIn;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.FillDetails.ReadWriteUserDetails;
import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

import javax.annotation.Nullable;

public class ConnectingActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText emailEditText;
    private EditText passwordEditText;
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
    private static final String TAG = "ConnectingActivity";
    private boolean fromGoogle = false;
    ReadWriteUserDetails readWriteUserDetails;

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
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        checkVerification();
                    } else {
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException e) {
                            emailEditText.setError("Email doesn't exist. Please register again.");
                            emailEditText.requestFocus();
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            passwordEditText.setError("Email and password doesn't match. Please re-enter again.");
                            passwordEditText.requestFocus();
                        } catch (Exception e) {
                            Log.e(TAG, e.getMessage());
                            Toast.makeText(ConnectingActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
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
                    long currTime = System.currentTimeMillis();

                    HashMap<String, Object> map = new HashMap<>();
                    String[] parts = user.getDisplayName().split(" ");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < parts.length; i++) {
                        sb.append(parts[i]);
                    }
                    map.put("firstName", parts[0].toString());
                    map.put("lastName", sb.toString());
                    map.put("profilePic", user.getPhotoUrl().toString());
                    map.put("email", user.getEmail());

                    boolean isNewUser = (currTime < dateCreate + epsTime);

                    Intent intent;
                    if (isNewUser) {
                        intent = new Intent(ConnectingActivity.this, SignUpActivity.class);
                        intent.putExtra("userData", map);
                        intent.putExtra("fromGoogle", fromGoogle);
                    } else {
                        updateReadWriteUserDetails();
                        intent = new Intent(ConnectingActivity.this, HomeBottomNavigation.class);
                    }
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(ConnectingActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkVerification() {
        FirebaseUser user = auth.getCurrentUser();
        if (user.isEmailVerified()) {
            updateReadWriteUserDetails();
            intent = new Intent(ConnectingActivity.this, HomeBottomNavigation.class);
            startActivity(intent);
            finish();
        } else {
            user.sendEmailVerification();
            auth.signOut();
            showAlertDialog();
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

    private void updateReadWriteUserDetails() {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String uid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails userDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (userDetails != null) {

                    readWriteUserDetails = ReadWriteUserDetails.getInstance(ConnectingActivity.this);
                    // Update the fields of the userDetails object
                    readWriteUserDetails.setFirstName(snapshot.child("firstName").getValue(String.class));
                    readWriteUserDetails.setLastName(snapshot.child("lastName").getValue(String.class));
                    readWriteUserDetails.setEmail(snapshot.child("email").getValue(String.class));
                    readWriteUserDetails.setBirthday(snapshot.child("birthday").getValue(String.class));
                    readWriteUserDetails.setPhoneNumber(snapshot.child("phoneNumber").getValue(String.class));
                    readWriteUserDetails.setAddress(snapshot.child("address").getValue(String.class));
                    readWriteUserDetails.setGender(snapshot.child("gender").getValue(String.class));
                    readWriteUserDetails.setPassword(snapshot.child("password").getValue(String.class));
                    readWriteUserDetails.setPurpose(snapshot.child("purpose").getValue(String.class));

                    // Save the updated userDetails object
                    readWriteUserDetails.saveUserDetails();


                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ConnectingActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}