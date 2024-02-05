package com.example.realestatehub;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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
import java.util.List;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private ImageView userImageView;
    private Button editImageView;
    private Button continueButton;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private EditText birthdayEditText;
    private EditText phoneNumberEditText;
    private CountryCodePicker cpp;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> adapter;
    private Handler handler;
    private String currentQuery = "";
    private static final String TAG = "SignUpActivity";
    private Intent intent;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_sign_up);

        initUI();
        addressInit();
    }

    private void initUI() {
        auth = FirebaseAuth.getInstance();

        cpp = findViewById(R.id.countryCodeSpinner);
        firstNameEditText = findViewById(R.id.firstNameEditText);
        lastNameEditText = findViewById(R.id.lastNameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        birthdayEditText = findViewById(R.id.birthdayEditText);
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText);
        genderRadioGroup = findViewById(R.id.genderRadioGroup);
        autoCompleteTextView = findViewById(R.id.addressAutoCompleteTextView);
        backButton = findViewById(R.id.backButton);
        editImageView = findViewById(R.id.editImageView);
        userImageView = findViewById(R.id.userImageView);
        continueButton = findViewById(R.id.continueButton);
        backButton.setOnClickListener(this);
        continueButton.setOnClickListener(this);
        birthdayEditText.setOnClickListener(this);
        userImageView.setOnClickListener(this);
        editImageView.setOnClickListener(this);
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
            intent = new Intent(this, ConnectingActivity.class);
            startActivity(intent);
            finish();
        } else if (viewId == R.id.userImageView || viewId == R.id.editImageView) {
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
        }
        if (TextUtils.isEmpty(lastName)) {
            lastNameEditText.setError("Last name is required");
            lastNameEditText.requestFocus();

        }
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, email, Toast.LENGTH_SHORT).show();
            emailEditText.setError("Email is required");
            emailEditText.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Valid Email is required");
            emailEditText.requestFocus();

        }
        if (TextUtils.isEmpty(password)) {
            passwordEditText.setError("Password is required");
            passwordEditText.requestFocus();
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEditText.setError("Confirm password is required");
            confirmPasswordEditText.requestFocus();
        } else if (password.length() < 6) {
            passwordEditText.setError("Password too short");
            passwordEditText.requestFocus();
        } else if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.setError("Password Confirmation is required");
            confirmPasswordEditText.requestFocus();
        }

        if (TextUtils.isEmpty(birthday)) {
            birthdayEditText.setError("Birthday is required");
            birthdayEditText.requestFocus();
        } else if (phoneNumber.length() != 10) {
            phoneNumberEditText.setError("Valid phone number is required");
            phoneNumberEditText.requestFocus();
        } else if (TextUtils.isEmpty(address)) {
            autoCompleteTextView.setError("Address is required");
            autoCompleteTextView.requestFocus();
        } else if (genderRadioGroup.getCheckedRadioButtonId() == -1) {
            genderRadioButton.setError("Gender is required");
            genderRadioButton.requestFocus();
        } else {
            gender = genderRadioButton.getText().toString();
            registerUser(firstName, lastName, email, password, birthday, phoneNumber, gender, address);
        }
    }

    private void registerUser(String firstName, String lastName, String email, String password, String birthday, String phoneNumber, String gender, String address) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    Toast.makeText(SignUpActivity.this, "User has been registered successfully", Toast.LENGTH_SHORT).show();
                    firebaseUser = auth.getCurrentUser();
                    UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(firstName + " " + lastName).build();
                    firebaseUser.updateProfile(profileChangeRequest);

                    //Enter User data into the firebase Realtime Database
                    ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails();
                    writeUserDetails.setFirstName(firstName);
                    writeUserDetails.setLastName(lastName);
                    writeUserDetails.setEmail(email);
                    writeUserDetails.setPassword(password);
                    writeUserDetails.setBirthday(birthday);
                    writeUserDetails.setPhoneNumber(phoneNumber);
                    writeUserDetails.setAddress(address);
                    writeUserDetails.setGender(gender);

                    //Extracting User reference from Database from "Registered Users"
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
                    reference.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                firebaseUser.sendEmailVerification();
                                showAlertDialog();
                            } else {
                                Toast.makeText(SignUpActivity.this, "User registration failed. Please try again", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                } else {
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        passwordEditText.setError("Your password should be at least 6 digits");
                        passwordEditText.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        emailEditText.setError("Valid email is required or already in use");
                        emailEditText.requestFocus();
                    } catch (FirebaseAuthUserCollisionException e) {
                        emailEditText.setError("Email is already registered");
                        emailEditText.requestFocus();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }

        });
    }

    private void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email Verification");
        builder.setMessage("Please check your email and verify your account in order to log in.");
        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intent = new Intent(SignUpActivity.this, ConnectingActivity.class);
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
