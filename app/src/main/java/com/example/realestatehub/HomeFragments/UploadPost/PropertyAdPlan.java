package com.example.realestatehub.HomeFragments.UploadPost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.realestatehub.FillDetails.ReadWritePostDetails;
import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.HomeFragments.UploadPost.GooglePay.CheckoutActivity;
import com.example.realestatehub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PropertyAdPlan extends AppCompatActivity implements View.OnClickListener {
    private Button backButton, freeAdButton, paidAdButton;
    private Intent intent;
    protected ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private ImageView diamodImageView;
    private boolean freeAdButtonClicked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_property_ad_plan);
        initUI();
    }


    private void initUI() {
        diamodImageView = findViewById(R.id.diamondImageView);
        Glide.with(this).asGif().load(R.drawable.diamond).into(diamodImageView);

        backButton = findViewById(R.id.backButton);
        freeAdButton = findViewById(R.id.freeAdButton);
        paidAdButton = findViewById(R.id.paidAdButton);
        auth = FirebaseAuth.getInstance();

        backButton.setOnClickListener(this);
        freeAdButton.setOnClickListener(this);
        paidAdButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.backButton) {
            intent = new Intent(this, PropertyAddPhotosVideos.class);
            startActivity(intent);
            finish();
        } else if (viewId == R.id.freeAdButton) {
            freeAdButtonClicked = true;
            readWritePostDetails.setAdType(readWritePostDetails.getAdID(), "Free");
            ChangeIntent();
        } else if (viewId == R.id.paidAdButton) {
            freeAdButtonClicked = false;
            readWritePostDetails.setAdType(readWritePostDetails.getAdID(), "Paid");
            ChangeIntent();
        }
    }

    public void onBackPressed() {
        intent = new Intent(this, AddPostActivity.class);
        startActivity(intent);
        finish();
    }

    private void ChangeIntent() {
        firebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Posts").child(firebaseUser.getUid()).child(readWritePostDetails.getAdID()).child("Property Details");
        reference.setValue(readWritePostDetails.getPostDetails(readWritePostDetails.getAdID())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (freeAdButtonClicked) {
                        intent = new Intent(PropertyAdPlan.this, HomeBottomNavigation.class);
                    } else {
                        intent = new Intent(PropertyAdPlan.this, CheckoutActivity.class);
                    }
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

}
