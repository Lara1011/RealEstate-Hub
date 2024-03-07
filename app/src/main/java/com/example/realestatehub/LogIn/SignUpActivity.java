package com.example.realestatehub.LogIn;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SignUpActivity";
    private Button backButton, editImageViewButton, continueButton;
    private ImageView userImageView;
    private EditText firstNameEditText, lastNameEditText, emailEditText, passwordEditText, confirmPasswordEditText, birthdayEditText, phoneNumberEditText;
    private CountryCodePicker cpp;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private String currentQuery = "";
    private boolean fromGoogle = false;
    private Handler handler;
    private Intent intent;
    private Database database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_sign_up);

        initUI();
        addressInit();
    }

    private void initUI() {
        database = new Database(this);

        backButton = findViewById(R.id.backButton);
        continueButton = findViewById(R.id.continueButton);
        editImageViewButton = findViewById(R.id.editImageViewButton);

        userImageView = findViewById(R.id.userImageView);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        cpp = findViewById(R.id.countryCodeSpinner);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        autoCompleteTextView = findViewById(R.id.addressAutoCompleteTextView);

        backButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        editImageViewButton.setOnClickListener(this);
        birthdayEditText.setOnClickListener(this);
        userImageView.setOnClickListener(this);

        // Retrieve user data from intent
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("fromGoogle")) {
            fromGoogle = intent.getBooleanExtra("fromGoogle", true);
        }
        if (intent != null && intent.hasExtra("userData")) {
            HashMap<String, Object> userData = (HashMap<String, Object>) intent.getSerializableExtra("userData");
            if (userData != null) {
                firstNameEditText.setText((String) userData.get("firstName"));
                lastNameEditText.setText((String) userData.get("lastName"));
                emailEditText.setText((String) userData.get("email"));
            }
        }
        birthdayEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                formatBirthdayText(editable);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        intent = new Intent(this, ConnectingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.continueButton) {
            obtainData();
        } else if (viewId == R.id.birthdayEditText) {
            pickBirthday();
        } else if (viewId == R.id.backButton) {
            onBackPressed();
        } else if (viewId == R.id.userImageView || viewId == R.id.editImageViewButton) {
//            Intent photoIntent = new Intent(Intent.ACTION_PICK);
//            photoIntent.setType("image/*");
//            startActivityForResult(photoIntent, 1);
//            uploadImage();
        }
    }

    private void formatBirthdayText(Editable editable) {
        String input = editable.toString();
        if (input.length() == 2 || input.length() == 5) {
            editable.append(',');
        }
    }

    private void pickBirthday() {
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                birthdayEditText.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        }, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }

    private void obtainData() {
        boolean isValid = true;

        int genderRadioId = genderRadioGroup.getCheckedRadioButtonId();
        genderRadioButton = findViewById(genderRadioId);
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String confirmPassword = confirmPasswordEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String address = autoCompleteTextView.getText().toString();
        String gender;

        if (TextUtils.isEmpty(firstName)) {
            firstNameEditText.setError("First name is required");
            firstNameEditText.requestFocus();
            isValid = false;
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();
            isValid = false;
        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid Email is required");
            emailEditText.requestFocus();
            isValid = false;
        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
            isValid = false;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Confirm password is required");
            confirmPasswordEditText.requestFocus();
            isValid = false;
        } else if (password.length() < 6) {
            passwordEditText.setError("Password too short");
            passwordEditText.requestFocus();
            isValid = false;
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password Confirmation is required");
            confirmPasswordEditText.requestFocus();
            isValid = false;
        }
        if (TextUtils.isEmpty(birthday)) {
            birthdayEditText.setError("Birthday is required");
            birthdayEditText.requestFocus();
            isValid = false;
        }
        if (phoneNumber.length() != 10) {
            phoneNumberEditText.setError("Valid phone number is required");
            phoneNumberEditText.requestFocus();
            isValid = false;
        }
        if (TextUtils.isEmpty(address)) {
            autoCompleteTextView.setError("Address is required");
            autoCompleteTextView.requestFocus();
            isValid = false;
        }
        if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
            genderRadioButton.setError("Gender is required");
            genderRadioButton.requestFocus();
            isValid = false;
        }
        if (isValid) {
            gender = genderRadioButton.getText().toString();
            if (fromGoogle) {
                database.registerUserFromGoogle(firstName, lastName, email, password, birthday, phoneNumber, gender, address, new Database.GeneralCallback() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(SignUpActivity.this, SetIntentActivity.class);
                        intent.putExtra("fromGoogle", fromGoogle);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        if (errorCode == 0) {
                            Toast.makeText(SignUpActivity.this, errorCode, Toast.LENGTH_SHORT).show();
                        } else if (errorCode == 1) {
                            Toast.makeText(SignUpActivity.this, errorCode, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                database.registerUser(firstName, lastName, email, password, birthday, phoneNumber, gender, address, new Database.GeneralCallback() {

                    @Override
                    public void onSuccess() {
                        showAlertDialog();
                    }

                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        Log.e(TAG, "Registration failed: " + errorMessage);
                        if (errorMessage != null && !errorMessage.isEmpty()) {
                            if (errorCode == 1) {
                                passwordEditText.setError(errorMessage);
                                passwordEditText.requestFocus();
                            } else if (errorCode == 2) {
                                emailEditText.setError(errorMessage);
                                emailEditText.requestFocus();
                            } else if (errorCode == 3) {
                                emailEditText.setError(errorMessage);
                                emailEditText.requestFocus();
                            }
                            Toast.makeText(SignUpActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SignUpActivity.this, "User registration failed. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email Verification");
        builder.setMessage("Please check your email and verify your account in order to log in.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent = new Intent(SignUpActivity.this, SetIntentActivity.class);
                startActivity(intent);
                finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void addressInit() {
        // In your activity or fragment
        autoCompleteTextView = findViewById(R.id.addressAutoCompleteTextView);
        // Set up adapter for autocomplete
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentQuery = charSequence.toString();
                handler.removeMessages(0);
                handler.sendEmptyMessage(0);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message message) {
                new FetchAddressTask().execute(currentQuery);
                return true;
            }
        });
    }

    private class FetchAddressTask extends AsyncTask<String, Void, List<String>> {
        @Override
        protected List<String> doInBackground(String... params) {
            try {
                String encodedQuery = URLEncoder.encode(params[0], "UTF-8");
                String apiUrl = "https://nominatim.openstreetmap.org/search?q=" +
                        encodedQuery + "&format=json&limit=5";
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(
                        new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }

                return parseAddressSuggestions(response.toString());
            } catch (IOException e) {
                Log.e("OpenStreetMap", "Error fetching address suggestions", e);
                return new ArrayList<>();
            }
        }

        @Override
        protected void onPostExecute(List<String> addressSuggestions) {
            // Update the adapter with the new suggestions
            adapter.clear();
            adapter.addAll(addressSuggestions);
            adapter.notifyDataSetChanged();
        }

        private List<String> parseAddressSuggestions(String response) {
            List<String> suggestions = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonAddress = jsonArray.getJSONObject(i);
                    String address = jsonAddress.getString("display_name");
                    suggestions.add(address);
                }
            } catch (JSONException e) {
                Log.e("OpenStreetMap", "Error parsing address suggestions", e);
            }
            return suggestions;
        }
    }
}
