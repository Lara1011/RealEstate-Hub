package com.example.realestatehub.HomeFragments.UploadPost;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.Utils.ReadWritePostDetails;
import com.example.realestatehub.R;

import java.util.HashMap;

public class PropertyFillInformation extends AppCompatActivity implements View.OnClickListener{
    private Button backButton;
    private Button continueButton;
    private EditText numberOfRoomsEditText;
    private EditText additionalInformationEditText;
    private CheckBox accessForDisabledCheckBox;
    private CheckBox FurnitureCheckBox;
    private CheckBox ElevatorsCheckBox;
    private CheckBox airConditionerCheckBox;
    private CheckBox kosherkitchenCheckBox;
    private CheckBox StorageCheckBox;
    private CheckBox waterHeaterCheckBox;
    private CheckBox RenovatedCheckBox;
    private Spinner showerNumberSpinner;
    private Spinner numberOfParkingSpinner;
    private Spinner numberOfBalconiesSpinner;
    private ArrayAdapter<CharSequence> showerNumberAdapter;
    private ArrayAdapter<CharSequence> numberOfParkingAdapter;
    private ArrayAdapter<CharSequence> numberOfBalconiesAdapter;
    private Intent intent;
    protected ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_property_information);
        setSpinners();
        initUI();
        loadSavedData();
    }

    private void setSpinners() {
        //Spinner for shower number
        showerNumberSpinner = findViewById(R.id.showerNumberSpinner);
        showerNumberSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        showerNumberAdapter = ArrayAdapter.createFromResource(this, R.array.shower_number, android.R.layout.simple_spinner_item);
        showerNumberAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        showerNumberSpinner.setAdapter(showerNumberAdapter);

        //Spinner for number of parking
        numberOfParkingSpinner = findViewById(R.id.numberOfParkingSpinner);
        numberOfParkingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        numberOfParkingAdapter = ArrayAdapter.createFromResource(this, R.array.number_of_parking_blaconies, android.R.layout.simple_spinner_item);
        numberOfParkingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfParkingSpinner.setAdapter(numberOfParkingAdapter);


        //Spinner for number of balconies
        numberOfBalconiesSpinner = findViewById(R.id.numberOfBalconiesSpinner);
        numberOfBalconiesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        numberOfBalconiesAdapter = ArrayAdapter.createFromResource(this, R.array.number_of_parking_blaconies, android.R.layout.simple_spinner_item);
        numberOfBalconiesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        numberOfBalconiesSpinner.setAdapter(numberOfBalconiesAdapter);
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);
        numberOfRoomsEditText = findViewById(R.id.numberOfRoomsEditText);
        additionalInformationEditText = findViewById(R.id.additionalInformationEditText);
        accessForDisabledCheckBox = findViewById(R.id.accessForDisabledCheckBox);
        airConditionerCheckBox = findViewById(R.id.airConditionerCheckBox);
        RenovatedCheckBox = findViewById(R.id.RenovatedCheckBox);
        StorageCheckBox = findViewById(R.id.StorageCheckBox);
        waterHeaterCheckBox = findViewById(R.id.waterHeaterCheckBox);
        kosherkitchenCheckBox = findViewById(R.id.kosherkitchenCheckBox);
        FurnitureCheckBox = findViewById(R.id.FurnitureCheckBox);
        ElevatorsCheckBox = findViewById(R.id.ElevatorsCheckBox);

        continueButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.continueButton) {
            obtainData();
        } else if (viewId == R.id.backButton) {
            intent = new Intent(this, AddPostActivity.class);
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
        String numberOfRooms = numberOfRoomsEditText.getText().toString();
        String additionalInformation = additionalInformationEditText.getText().toString();
        String accessForDisabled = String.valueOf(accessForDisabledCheckBox.isChecked());
        String airConditioner = String.valueOf(airConditionerCheckBox.isChecked());
        String Renovated = String.valueOf(RenovatedCheckBox.isChecked());
        String Storage = String.valueOf(StorageCheckBox.isChecked());
        String waterHeater = String.valueOf(waterHeaterCheckBox.isChecked());
        String kosherkitchen = String.valueOf(kosherkitchenCheckBox.isChecked());
        String Furniture = String.valueOf(FurnitureCheckBox.isChecked());
        String Elevators = String.valueOf(ElevatorsCheckBox.isChecked());
        String numberOfBalconies = numberOfBalconiesSpinner.getSelectedItem().toString();
        String numberOfParking = numberOfParkingSpinner.getSelectedItem().toString();
        String showerNumber = showerNumberSpinner.getSelectedItem().toString();

        if (TextUtils.isEmpty(numberOfRooms)) {
            numberOfRoomsEditText.setError("Number of rooms is required");
            numberOfRoomsEditText.requestFocus();
        }
        else {
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Number Of Rooms", numberOfRooms);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Shower Number", showerNumber);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Number Of Parking", numberOfParking);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Number Of Balconies", numberOfBalconies);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Additional Information", additionalInformation);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Access For Disabled", accessForDisabled);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Air Conditioner", airConditioner);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Renovated", Renovated);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Storage", Storage);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Water Heater", waterHeater);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Kosher Kitchen", kosherkitchen);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Furniture", Furniture);
            readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(), "Elevators", Elevators);

            intent = new Intent(PropertyFillInformation.this, PropertyPriceAndSize.class);
            startActivity(intent);
            finish();
        }
    }
    private void loadSavedData() {
        // Load saved data from ReadWritePostDetails
        HashMap<String, String> postDetails = readWritePostDetails.getPostDetails(readWritePostDetails.getAdID());

        if (postDetails != null) {
            numberOfRoomsEditText.setText(getSafeValue(postDetails, "Number Of Rooms"));
            String balconies = getSafeValue(postDetails, "Number Of Balconies");
            if (!balconies.isEmpty()) {
                int position = numberOfBalconiesAdapter.getPosition(balconies);
                numberOfBalconiesSpinner.setSelection(position);
            }
            String parking = getSafeValue(postDetails, "Number Of Parking");
            if (!parking.isEmpty()) {
                int position = numberOfParkingAdapter.getPosition(parking);
                numberOfParkingSpinner.setSelection(position);
            }
            String shower = getSafeValue(postDetails, "Shower Number");
            if (!parking.isEmpty()) {
                int position = showerNumberAdapter.getPosition(shower);
                showerNumberSpinner.setSelection(position);
            }
            additionalInformationEditText.setText(getSafeValue(postDetails, "Additional Information"));
            accessForDisabledCheckBox.setChecked(getSafeValue(postDetails, "Access For Disabled").equals("true"));
            airConditionerCheckBox.setChecked(getSafeValue(postDetails, "Air Conditioner").equals("true"));
            RenovatedCheckBox.setChecked(getSafeValue(postDetails, "Renovated").equals("true"));
            StorageCheckBox.setChecked(getSafeValue(postDetails, "Storage").equals("true"));
            waterHeaterCheckBox.setChecked(getSafeValue(postDetails, "Water Heater").equals("true"));
            kosherkitchenCheckBox.setChecked(getSafeValue(postDetails, "Kosher Kitchen").equals("true"));
            FurnitureCheckBox.setChecked(getSafeValue(postDetails, "Furniture").equals("true"));
            ElevatorsCheckBox.setChecked(getSafeValue(postDetails, "Elevators").equals("true"));
        }
    }

    // Helper method to safely retrieve values
    private String getSafeValue(HashMap<String, String> map, String key) {
        return map.containsKey(key) ? map.get(key) : "";
    }
}
