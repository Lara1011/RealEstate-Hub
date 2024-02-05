package com.example.realestatehub;

import static android.app.Activity.RESULT_OK;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProfileFragment extends Fragment implements View.OnClickListener {
    private TextView textView_seller_or_buyer, textView_show_email, textView_show_dob, textView_show_gender, textView_show_mobile, textView_show_register_date, textView_show_welcome;
    private String purpose, email, dob, gender, mobile;
    private Button editImageView,signOutButton;
    private ImageView userImageView;
    private View view;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private FirebaseUser firebaseUser;
    private Uri uriImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        initUI();
        return view;
    }

    private void initUI() {
        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Something Went Wrong!", Toast.LENGTH_SHORT).show();
        } else {
            fetchUserDataFromFirebase(user);
        }
        storageReference = FirebaseStorage.getInstance().getReference("User Profile Images");
        userImageView = view.findViewById(R.id.userImageView);
        textView_seller_or_buyer = view.findViewById(R.id.textView_seller_or_buyer);
        textView_show_email = view.findViewById(R.id.textView_show_email);
        textView_show_dob = view.findViewById(R.id.textView_show_dob);
        textView_show_gender = view.findViewById(R.id.textView_show_gender);
        textView_show_mobile = view.findViewById(R.id.textView_show_mobile);
        textView_show_register_date = view.findViewById(R.id.textView_show_register_date);
        textView_show_welcome = view.findViewById(R.id.textView_show_welcome);
        editImageView = view.findViewById(R.id.editImageView);
        signOutButton = view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(this);
        editImageView.setOnClickListener(this);
        userImageView.setOnClickListener(this);

        Uri uri = user.getPhotoUrl();
        Picasso.get().load(uri).into(userImageView);
    }

    private void fetchUserDataFromFirebase(FirebaseUser user) {
        String uid = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference("Registered Users");
        reference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readWriteUserDetails = snapshot.getValue(ReadWriteUserDetails.class);
                if (readWriteUserDetails != null) {
                    textView_show_welcome.setText(String.format("Welcome " + readWriteUserDetails.getFirstName()));
//                    name = readWriteUserDetails.getFirstName() + " " + readWriteUserDetails.getLastName();
                    email = readWriteUserDetails.getEmail();
                    dob = readWriteUserDetails.getBirthday();
                    gender = readWriteUserDetails.getGender();
                    mobile = readWriteUserDetails.getPhoneNumber();
                    purpose = readWriteUserDetails.getPurpose();
                    textView_seller_or_buyer.setText(purpose);
                    textView_show_email.setText(email);
                    textView_show_dob.setText(dob);
                    textView_show_gender.setText(gender);
                    textView_show_mobile.setText(mobile);

//                    Uri uri = firebaseUser.getPhotoUrl();
//                    Picasso.get().load(uri).into(userImageView);

                    long dateLastLogin = user.getMetadata().getLastSignInTimestamp();
                    DateFormat obj = new SimpleDateFormat("dd MMM yyyy HH:mm");
                    // we create instance of the Date and pass milliseconds to the constructor
                    Date res = new Date(dateLastLogin);
                    textView_show_register_date.setText(String.valueOf(obj.format(res)));
                }else{
                    Toast.makeText(getContext(), "SomeThing Went Wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Something Went Wrong2!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.userImageView) {
            openFileChooser();
        } else if (v.getId() == R.id.editImageView) {
            uploadPic();
        } else if(v.getId() == R.id.signOutButton){
            auth.signOut();
            Intent intent = new Intent(getContext(), ConnectingActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    private void uploadPic() {
        if (uriImage != null) {
            StorageReference fileRef = storageReference.child(auth.getCurrentUser().getUid() + "." + getFileExtension(uriImage));
            fileRef.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            firebaseUser = auth.getCurrentUser();
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                            firebaseUser.updateProfile(profileChangeRequest);
                        }
                    });
                }
            });
            Toast.makeText(getContext(), "Upload Success", Toast.LENGTH_SHORT).show();
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            userImageView.setImageURI(uriImage);
        }
    }
}