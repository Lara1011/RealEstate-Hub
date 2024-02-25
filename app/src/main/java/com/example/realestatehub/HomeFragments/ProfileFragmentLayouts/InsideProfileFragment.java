package com.example.realestatehub.HomeFragments.ProfileFragmentLayouts;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.realestatehub.FillDetails.ReadWriteUserDetails;
import com.example.realestatehub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.squareup.picasso.Picasso;

public class InsideProfileFragment extends Fragment implements View.OnClickListener {
    private Button backButton, saveOrEditButton;
    private ImageView userImageView;
    private EditText firstNameEditText, lastNameEditText, emailEditText, birthdayEditText, phoneNumberEditText, addressEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton genderRadioButton;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;
    private Uri imageUri;
    private View view;
    private boolean editing = true;
    private ReadWriteUserDetails readWriteUserDetails = ReadWriteUserDetails.getInstance(getContext());

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

        setInputFields(false);

        backButton.setOnClickListener(this);
        saveOrEditButton.setOnClickListener(this);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        if (firebaseUser == null) {
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
        String uid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {
                    firstNameEditText.setText(readWriteUserDetails.getFirstName());
                    lastNameEditText.setText(readWriteUserDetails.getLastName());
                    emailEditText.setText(readWriteUserDetails.getEmail());
                    birthdayEditText.setText(readWriteUserDetails.getBirthday());
                    phoneNumberEditText.setText(readWriteUserDetails.getPhoneNumber());
                    addressEditText.setText(readWriteUserDetails.getAddress());

                    if (readWriteUserDetails.getGender().equals("Male")) {
                        genderRadioGroup.check(R.id.maleRadioButton);
                    } else {
                        genderRadioGroup.check(R.id.femaleRadioButton);
                    }
                    //userImageView
                    imageUri = firebaseUser.getPhotoUrl();
                    Picasso.get().load(imageUri).into(userImageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pushUserDetailsToFirebase() {
        String uid = firebaseUser.getUid();
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

        readWriteUserDetails.setFirstName(firstName);
        readWriteUserDetails.setLastName(lastName);
        readWriteUserDetails.setEmail(email);
        readWriteUserDetails.setBirthday(birthday);
        readWriteUserDetails.setPhoneNumber(phoneNumber);
        readWriteUserDetails.setAddress(address);
        readWriteUserDetails.setGender(gender);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(uid).setValue(readWriteUserDetails);
    }
}