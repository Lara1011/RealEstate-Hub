package com.example.realestatehub;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class PropertyPriceAndSize extends AppCompatActivity implements View.OnClickListener{
    private Button backButton;
    private Button continueButton;
    private EditText TotalSizeEditText;
    private EditText PriceEditText;
    private EditText entryDateEditText;
    private CheckBox ImmediateCheckBox;
    private CheckBox FlexibleCheckBox;
    private Intent intent;
    protected ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2_property_price_and_size);

        initUI();
    }

    private void initUI() {
        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);
        TotalSizeEditText = findViewById(R.id.TotalSizeEditText);
        PriceEditText = findViewById(R.id.PriceEditText);
        entryDateEditText = findViewById(R.id.entryDateEditText);
        ImmediateCheckBox = findViewById(R.id.ImmediateCheckBox);
        FlexibleCheckBox = findViewById(R.id.FlexibleCheckBox);

        continueButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        entryDateEditText.setOnClickListener(this);
        entryDateEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatentryDateEditText(editable);
            }
        });
    }
    private void formatentryDateEditText(Editable editable) {
        String input = editable.toString();
        if (input.length() == 2 || input.length() == 5) {
            editable.append(',');
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.continueButton) {
            obtainData();
        } else if (viewId == R.id.backButton) {
            intent = new Intent(this, PropertyFillInformation.class);
            startActivity(intent);
            finish();
        } else if(viewId == R.id.entryDateEditText) {
            pickEntryDate();
        }
    }

    public void onBackPressed() {
        intent = new Intent(this, AddPostActivity.class);
        startActivity(intent);
        finish();
    }

    private void obtainData() {
        String TotalSize = TotalSizeEditText.getText().toString();
        String Price = PriceEditText.getText().toString();
        String entryDate = entryDateEditText.getText().toString();

        if (TextUtils.isEmpty(TotalSize)) {
            TotalSizeEditText.setError("Total size is required");
            TotalSizeEditText.requestFocus();
        }

        if (TextUtils.isEmpty(Price)) {
            PriceEditText.setError("Location of property is required");
            PriceEditText.requestFocus();
        }

        if (TextUtils.isEmpty(entryDate)) {
            entryDateEditText.setError("Entry date is required");
            entryDateEditText.requestFocus();
        }
        readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(),"TotalSize", TotalSize);
        readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(),"Price", Price);
        readWritePostDetails.addPostInformation(readWritePostDetails.getAdID(),"EntryDate", entryDate);
        intent = new Intent(this, PropertyAddPhotosVideos.class);
        startActivity(intent);
        finish();
    }

    private void pickEntryDate() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                entryDateEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);

        // Set the minimum date to the current date
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        datePickerDialog.show();
    }

}
