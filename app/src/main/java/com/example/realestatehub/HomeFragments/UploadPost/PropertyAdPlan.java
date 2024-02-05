package com.example.realestatehub.HomeFragments.UploadPost;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.FillDetails.ReadWritePostDetails;
import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class PropertyAdPlan extends AppCompatActivity implements View.OnClickListener{
    private Button backButton;
    private Button freeAdButton;
    private Button paidAdButton;
    private Intent intent;
    protected ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_property_ad_plan);
        initUI();
    }



    private void initUI() {
        backButton = findViewById(R.id.backButton);
        freeAdButton = findViewById(R.id.FreeAdButton);
        paidAdButton = findViewById(R.id.PaidAdButton);
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
        } else if (viewId == R.id.FreeAdButton) {
            readWritePostDetails.setAdType(readWritePostDetails.getAdID(),"Free");
            clickedFreeAd();
        } else if (viewId == R.id.PaidAdButton) {
            readWritePostDetails.setAdType(readWritePostDetails.getAdID(),"Paid");
            clickedPaidAd();
        }
    }
    public void onBackPressed() {
        intent = new Intent(this, AddPostActivity.class);
        startActivity(intent);
        finish();
    }

    private void clickedFreeAd(){
        firebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Posts");

        reference.child(firebaseUser.getUid() + "_" + readWritePostDetails.getAdID()).setValue(readWritePostDetails.getPostDetails(readWritePostDetails.getAdID())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    intent = new Intent(PropertyAdPlan.this, HomeBottomNavigation.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private void clickedPaidAd(){
        firebaseUser = auth.getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Posts");

        reference.child(firebaseUser.getUid() + "_" + readWritePostDetails.getAdID()).setValue(readWritePostDetails.getPostDetails(readWritePostDetails.getAdID())).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    intent = new Intent(PropertyAdPlan.this, HomeBottomNavigation.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}
