package com.example.realestatehub.HomeFragments.ProfileFragmentLayouts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realestatehub.LogIn.ConnectingActivity;
import com.example.realestatehub.R;

import com.example.realestatehub.Utils.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

public class InsideProfileFragment extends Fragment implements View.OnClickListener {
    private Button backButton, saveOrEditButton;
    private ImageView userImageView;
    private EditText firstNameEditText, lastNameEditText, emailEditText, birthdayEditText, phoneNumberEditText, addressEditText;
    private TextView deleteAccountTextView;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private Uri uriImage;
    private View view;
    private boolean editing = true;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inside_profile, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        backButton = view.findViewById(R.id.backButton);
        userImageView = view.findViewById(R.id.userImageView);
        firstNameEditText = view.findViewById(R.id.firstNameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        emailEditText = view.findViewById(R.id.emailEditText);
        birthdayEditText = view.findViewById(R.id.birthdayEditText);
        phoneNumberEditText = view.findViewById(R.id.phoneNumberEditText);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        int genderRadioId = genderRadioGroup.getCheckedRadioButtonId();
        genderRadioButton = view.findViewById(genderRadioId);
        saveOrEditButton = view.findViewById(R.id.saveOrEditButton);
        addressEditText = view.findViewById(R.id.addressEditText);
        deleteAccountTextView = view.findViewById(R.id.deleteAccountTextView);

        deleteAccountTextView.setOnClickListener(this);
        setInputFields(false);

        backButton.setOnClickListener(this);
        saveOrEditButton.setOnClickListener(this);

        database = Database.getInstance(getContext());

        if (database.getFirebaseUser() == null) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
        } else {
            fetchUserDetailsFromFirebase();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.saveOrEditButton) {
            if (editing) {
                setInputFields(true);
                Toast.makeText(getContext(), "Edit mode", Toast.LENGTH_SHORT).show();
            } else {
                setInputFields(false);
                Toast.makeText(getContext(), "View mode", Toast.LENGTH_SHORT).show();
            }
            pushUserDetailsToFirebase();
            editing = !editing;
        } else if (id == R.id.deleteAccountTextView) {
            database.deleteAccount();
            Intent intent = new Intent(getContext(), ConnectingActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else if (v.getId() == R.id.backButton) {
            navigateToProfileFragment();
        }
    }

    private void setInputFields(boolean editable) {
        userImageView.setClickable(editable);
        firstNameEditText.setFocusable(editable);
        lastNameEditText.setFocusable(editable);
        emailEditText.setFocusable(editable);
        birthdayEditText.setFocusable(editable);
        phoneNumberEditText.setFocusable(editable);
        genderRadioButton.setFocusable(editable);
        addressEditText.setFocusable(editable);

        firstNameEditText.setFocusableInTouchMode(editable);
        lastNameEditText.setFocusableInTouchMode(editable);
        emailEditText.setFocusableInTouchMode(editable);
        birthdayEditText.setFocusableInTouchMode(editable);
        phoneNumberEditText.setFocusableInTouchMode(editable);
        genderRadioButton.setFocusableInTouchMode(editable);
        addressEditText.setFocusableInTouchMode(editable);

        genderRadioGroup.setEnabled(editable);
        genderRadioGroup.getChildAt(0).setEnabled(editable);
        genderRadioGroup.getChildAt(1).setEnabled(editable);

        if (editable) {
            saveOrEditButton.setText(R.string.save);
        } else {
            saveOrEditButton.setText(R.string.edit);
        }
    }

    private void fetchUserDetailsFromFirebase() {
        database.fetchUserDetailsFromFirebase(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                firstNameEditText.setText(database.getReadWriteUserDetails().getFirstName());
                lastNameEditText.setText(database.getReadWriteUserDetails().getLastName());
                emailEditText.setText(database.getReadWriteUserDetails().getEmail());
                birthdayEditText.setText(database.getReadWriteUserDetails().getBirthday());
                phoneNumberEditText.setText(database.getReadWriteUserDetails().getPhoneNumber());
                addressEditText.setText(database.getReadWriteUserDetails().getAddress());

                if (database.getReadWriteUserDetails().getGender().equals("Male")) {
                    genderRadioGroup.check(R.id.maleRadioButton);
                } else {
                    genderRadioGroup.check(R.id.femaleRadioButton);
                }
                updateProfileUI();
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateProfileUI() {
        database.CheckCurrUserIfExists(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                uriImage = database.getFirebaseUser().getPhotoUrl();
                Picasso.get().load(uriImage).into(userImageView);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 0) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pushUserDetailsToFirebase() {
        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String birthday = birthdayEditText.getText().toString();
        String phoneNumber = phoneNumberEditText.getText().toString();
        String address = addressEditText.getText().toString();

        // Get the selected radio button's text for gender
        int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
        genderRadioButton = view.findViewById(selectedGenderId);
        String gender = genderRadioButton.getText().toString();

        database.pushUserDetailsToFirebase(firstName, lastName, email, birthday, phoneNumber, gender, address);
    }

    private void navigateToProfileFragment() {
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.profile);
        } else {
            Toast.makeText(getContext(), "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }

}
