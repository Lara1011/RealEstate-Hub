package com.example.realestatehub.HomeFragments.UploadPost;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.realestatehub.FillDetails.ReadWritePostDetails;
import com.example.realestatehub.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class PropertyAddPhotosVideos extends AppCompatActivity implements View.OnClickListener {
    private Button backButton;
    private Button continueButton;
    private Button uploadPhotosButton;
    private Button uploadVideosButton;
    private Intent intent;
    private ImageView postImageView;
    protected ReadWritePostDetails readWritePostDetails = ReadWritePostDetails.getInstance();
    private Uri uriImage;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser firebaseUser;


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
        postImageView = findViewById(R.id.postImageView);
        Glide.with(this).asGif().load(R.drawable.house).into(postImageView);

        continueButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        uploadPhotosButton.setOnClickListener(this);
        postImageView.setOnClickListener(this);

        //uploadVideosButton.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Users Posts Pictures").child(firebaseUser.getUid()).child(readWritePostDetails.getAdID());
        databaseReference = FirebaseDatabase.getInstance().getReference("Users Posts").child(firebaseUser.getUid()).child(readWritePostDetails.getAdID()).child("Photos");
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
        } else if (viewId == R.id.uploadPhotosButton) {
            uploadPic();
        } else if (viewId == R.id.postImageView) {
            openFileChooser();
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

    private void uploadPic() {
        if (uriImage != null) {
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Uploading...");
            dialog.show();
            storageReference.putFile(uriImage).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri downloadUri = uri;
                            String upload = databaseReference.push().getKey();
                            databaseReference.child(upload).setValue(downloadUri.toString());

                            // Dismiss the dialog here
                            dialog.dismiss();
                            Toast.makeText(PropertyAddPhotosVideos.this, "Upload Success", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    dialog.setMessage("Uploaded " + (int) percent + "%");
                }
            });
        }
    }


    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriImage = data.getData();
            Picasso.get().load(uriImage).into(postImageView);
        }
    }
}
