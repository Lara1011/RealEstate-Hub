package com.example.realestatehub.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;
import com.example.realestatehub.FillDetails.ReadWriteUserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SetIntentActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private ImageView makingDealImageView;
    private Button continueButton;
    private SwitchCompat sellerSwitch;
    private SwitchCompat buyerSwitch;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.back_ground_theme));

        setContentView(R.layout.activity1_set_intent);
        initUI();
    }

    private void initUI() {
        sellerSwitch = findViewById(R.id.sellerSwitch);
        buyerSwitch = findViewById(R.id.buyerSwitch);
        backButton = findViewById(R.id.backButton);
        makingDealImageView = findViewById(R.id.makingDealImageView);
        continueButton = findViewById(R.id.continueButton);

        backButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);

        Glide.with(this).asGif().load(R.drawable.making_deal).into(makingDealImageView);
    }

    @Override
    public void onClick(View v) {
        ReadWriteUserDetails writeUserDetails = ReadWriteUserDetails.getInstance();
        // Set purpose based on switch states
        if (v.getId() == R.id.continueButton) {
            if (sellerSwitch.isChecked() && buyerSwitch.isChecked()) {
                writeUserDetails.setPurpose("Seller and Buyer");
            } else if (sellerSwitch.isChecked()) {
                writeUserDetails.setPurpose("Seller");
            } else if (buyerSwitch.isChecked()) {
                writeUserDetails.setPurpose("Buyer");
            } else {
                // None selected, show a toast and return
                Toast.makeText(this, "Please choose at least one purpose", Toast.LENGTH_SHORT).show();
                return;
            }
            auth = FirebaseAuth.getInstance();

            FirebaseUser firebaseUser = auth.getCurrentUser();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
            reference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        //firebaseUser.sendEmailVerification();
                        //showAlertDialog();
                        Intent intent = new Intent(SetIntentActivity.this, ConnectingActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SetIntentActivity.this, "User registration failed. Please try again", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

}