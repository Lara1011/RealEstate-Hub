package com.example.realestatehub.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;

public class SetIntentActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton, continueButton;
    private ImageView makingDealImageView;
    private SwitchCompat sellerSwitch, buyerSwitch;
    private boolean fromGoogle = false;
    private Database database;


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
        // Retrieve user data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fromGoogle")) {
            fromGoogle = intent.getBooleanExtra("fromGoogle", true);
        }

        Glide.with(this).asGif().load(R.drawable.making_deal).into(makingDealImageView);

        database = new Database(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.continueButton) {
            if (sellerSwitch.isChecked() && buyerSwitch.isChecked()) {
                database.getReadWriteUserDetails().setPurpose("Seller and Buyer");
            } else if (sellerSwitch.isChecked()) {
                database.getReadWriteUserDetails().setPurpose("Seller");
            } else if (buyerSwitch.isChecked()) {
                database.getReadWriteUserDetails().setPurpose("Buyer");
            } else {
                Toast.makeText(this, "Please choose at least one purpose", Toast.LENGTH_SHORT).show();
                return;
            }
            database.setPurpose(fromGoogle, new Database.GeneralCallback() {
                @Override
                public void onSuccess() {
                    Intent intent = new Intent(SetIntentActivity.this, ConnectingActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(int errorCode, String errorMessage) {
                    if (errorCode == 0) {
                        Toast.makeText(SetIntentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    } else if (errorCode == 1) {
                        Toast.makeText(SetIntentActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
