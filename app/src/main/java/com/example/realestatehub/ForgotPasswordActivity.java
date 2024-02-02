package com.example.realestatehub;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class ForgotPasswordActivity extends AppCompatActivity {

     private Button continueBtn;
     private CardView smsCard,emailCard;

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
        continueBtn = findViewById(R.id.continue_btn);
        smsCard = findViewById(R.id.sms_cardview);
        emailCard = findViewById(R.id.email_cardview);

        smsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsCard.setBackgroundResource(R.drawable.bordered_cardview);
                emailCard.setBackgroundResource(R.drawable.default_cardview);
            }
        });

        emailCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                smsCard.setBackgroundResource(R.drawable.default_cardview);
                emailCard.setBackgroundResource(R.drawable.bordered_cardview);
            }
        });

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ForgotPasswordActivity.this, OTPCodeVerificationActivity.class));
            }
        });
    }
}