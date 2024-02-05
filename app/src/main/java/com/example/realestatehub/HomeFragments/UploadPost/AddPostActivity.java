package com.example.realestatehub.HomeFragments.UploadPost;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.FillDetails.ReadWritePostDetails;
import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

public class AddPostActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private Button continueButton;
    private EditText postNameEditText;
    private EditText propertyLocationEditText;
    private EditText streetNameTextView;
    private EditText propertyFloorEditText;
    private EditText totalFloorsEditText;
    private EditText homeNumberEditText;
    private Spinner propertyTypeSpinner;
    private Spinner viewSpinner;
    private ArrayAdapter<CharSequence> propertyTypeAdapter;
    private ArrayAdapter<CharSequence> viewAdapter;
    private Intent intent;
    private ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_property_address);
        setSpinners();
        initUI();
        readWritePostDetails.setAdID();
        loadSavedData();
    }

    private void setSpinners() {
        //Spinner for property type
        propertyTypeSpinner = findViewById(R.id.propertyTypeSpinner);
        propertyTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        propertyTypeAdapter = ArrayAdapter.createFromResource(this, R.array.property_types, android.R.layout.simple_spinner_item);
        propertyTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        propertyTypeSpinner.setAdapter(propertyTypeAdapter);

        //Spinner for view
        viewSpinner = findViewById(R.id.openViewSpinner);
        viewSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        viewAdapter = ArrayAdapter.createFromResource(this, R.array.open_view, android.R.layout.simple_spinner_item);
        viewAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        viewSpinner.setAdapter(viewAdapter);
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);
        postNameEditText = findViewById(R.id.postNameEditText);
        propertyLocationEditText = findViewById(R.id.propertyLocationEditText);
        streetNameTextView = findViewById(R.id.streetNameTextView);
        propertyFloorEditText = findViewById(R.id.propertyFloorEditText);
        totalFloorsEditText = findViewById(R.id.totalFloorsEditText);
        homeNumberEditText = findViewById(R.id.homeNumberEditText);

        auth = FirebaseAuth.getInstance();


        continueButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.continueButton) {
            obtainData();
        } else if (viewId == R.id.backButton) {
            intent = new Intent(this, HomeBottomNavigation.class);
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
        String postName = postNameEditText.getText().toString();
        String propertyLocation = propertyLocationEditText.getText().toString();
        String streetName = streetNameTextView.getText().toString();
        String propertyFloor = propertyFloorEditText.getText().toString();
        String totalFloors = totalFloorsEditText.getText().toString();
        String homeNumber = homeNumberEditText.getText().toString();
        String propertyType = propertyTypeSpinner.getSelectedItem().toString();
        String view = viewSpinner.getSelectedItem().toString();

        if (propertyType.equals("Apartment") || propertyType.equals("Building") || propertyType.equals("Roof/Penthouse")) {
            if (TextUtils.isEmpty(propertyFloor)) {
                propertyFloorEditText.setError("Floor is required");
                propertyFloorEditText.requestFocus();
            }

            if (TextUtils.isEmpty(totalFloors)) {
                propertyFloorEditText.setError("Total floors is required");
                propertyFloorEditText.requestFocus();
            }
        }
        else if(propertyType.equals("House")){
            if (TextUtils.isEmpty(totalFloors)) {
                propertyFloorEditText.setError("Total floors is required");
                propertyFloorEditText.requestFocus();
            }
            if (TextUtils.isEmpty(homeNumber)) {
                propertyFloorEditText.setError("Home number is required");
                propertyFloorEditText.requestFocus();
            }
        }

        if (TextUtils.isEmpty(postName)) {
            postNameEditText.setError("Post name is required");
            postNameEditText.requestFocus();
        } else if (TextUtils.isEmpty(propertyLocation)) {
            propertyLocationEditText.setError("Location of property is required");
            propertyLocationEditText.requestFocus();
        } else if (TextUtils.isEmpty(streetName)) {
            streetNameTextView.setError("Street name of property is required");
            streetNameTextView.requestFocus();
        } else {
            readWritePostDetails.addPost(readWritePostDetails.getAdID(), postName, propertyLocation, streetName, propertyFloor, totalFloors, homeNumber, propertyType, view);
            intent = new Intent(AddPostActivity.this, PropertyFillInformation.class);
            startActivity(intent);
            finish();
        }
    }

    private void loadSavedData() {
        // Load saved data from ReadWritePostDetails
        HashMap<String, String> postDetails = readWritePostDetails.getPostDetails(readWritePostDetails.getAdID());

        if (postDetails != null) {
            postNameEditText.setText(getSafeValue(postDetails, "name"));
            propertyLocationEditText.setText(getSafeValue(postDetails, "location"));
            streetNameTextView.setText(getSafeValue(postDetails, "streetName"));
            propertyFloorEditText.setText(getSafeValue(postDetails, "floor"));
            totalFloorsEditText.setText(getSafeValue(postDetails, "totalFloors"));
            homeNumberEditText.setText(getSafeValue(postDetails, "homeNumber"));

            String propertyType = getSafeValue(postDetails, "type");
            if (!propertyType.isEmpty()) {
                int position = propertyTypeAdapter.getPosition(propertyType);
                propertyTypeSpinner.setSelection(position);
            }

            String view = getSafeValue(postDetails, "view");
            if (!view.isEmpty()) {
                int position = viewAdapter.getPosition(view);
                viewSpinner.setSelection(position);
            }
        }
    }

    // Helper method to safely retrieve values
    private String getSafeValue(HashMap<String, String> map, String key) {
        return map.containsKey(key) ? map.get(key) : "";
    }
}
