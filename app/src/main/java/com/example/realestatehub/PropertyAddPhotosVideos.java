package com.example.realestatehub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class PropertyAddPhotosVideos extends AppCompatActivity implements View.OnClickListener{
    private Button backButton;
    private Button continueButton;
    private Button uploadPhotosButton;
    private Button uploadVideosButton;
    private Intent intent;
    protected ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_property_photos_videos);
        initUI();
        loadSavedData();
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);
        uploadPhotosButton = findViewById(R.id.uploadPhotosButton);
        uploadVideosButton = findViewById(R.id.uploadVideosButton);

        continueButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.continueButton) {
            obtainData();
        } else if (viewId == R.id.backButton) {
            intent = new Intent(this, PropertyPriceAndSize.class);
            startActivity(intent);
            finish();
        }
    }
    public void onBackPressed() {
        intent = new Intent(this, AddPostActivity.class);
        startActivity(intent);
        finish();
    }

    private void obtainData() {

        intent = new Intent(PropertyAddPhotosVideos.this, PropertyAdPlan.class);
        startActivity(intent);
        finish();
    }
    private void loadSavedData() {
        // Load saved data from ReadWritePostDetails

    }

    // Helper method to safely retrieve values
    private String getSafeValue(HashMap<String, String> map, String key) {
        return map.containsKey(key) ? map.get(key) : "";
    }
}
