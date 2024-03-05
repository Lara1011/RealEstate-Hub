package com.example.realestatehub.HomeFragments.ReadPostAdapter;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostDetailsActivity extends AppCompatActivity {
    private Database database;
    private boolean favoriteClicked = false;
    private void addToFavoriteDB(String itemId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Favorites");
        HashMap<String, Object> favoriteMap = new HashMap<>();
        favoriteMap.put("Post Id", itemId);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(itemId, favoriteMap);

        reference.child(uid).updateChildren(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void addToRecentlyDB(String itemId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Viewed Items");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> viewedItemMap = new HashMap<>();
        viewedItemMap.put("Post Id", itemId);
        viewedItemMap.put("viewedDate", currentDate);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(itemId, viewedItemMap);

        reference.child(uid).updateChildren(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void addToPurchasedDB(String itemId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Reached Items");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> purchasedItemMap = new HashMap<>();
        purchasedItemMap.put("Post Id", itemId);
        purchasedItemMap.put("reachedDate", currentDate);
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(itemId, purchasedItemMap);

        reference.child(uid).updateChildren(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        database = new Database(this);
        Intent intent = getIntent();
        if (intent != null) {
            HashMap<String, Object> post = (HashMap<String, Object>) intent.getSerializableExtra("Post Details");
            if (post != null) {
                addToRecentlyDB(getSafeValue(intent.getStringExtra("Post Id")));

                TextView userNameTextView = findViewById(R.id.userNameTextView);
                TextView phoneNumberTextView = findViewById(R.id.phoneNumberTextView);
                TextView nameTextView = findViewById(R.id.nameTextView);
                TextView locationTextView = findViewById(R.id.locationTextView);
                TextView streetNameTextView = findViewById(R.id.streetNameTextView);
                TextView floorTextView = findViewById(R.id.floorTextView);
                TextView totalFloorsTextView = findViewById(R.id.totalFloorsTextView);
                TextView homeNumberTextView = findViewById(R.id.homeNumberTextView);
                TextView typeTextView = findViewById(R.id.typeTextView);
                TextView viewTextView = findViewById(R.id.viewTextView);
                TextView adTypeTextView = findViewById(R.id.adTypeTextView);
                TextView additionalInformationTextView = findViewById(R.id.additionalInformationTextView);
                TextView propertyCharacteristicsTextView = findViewById(R.id.propertyCharacteristicsTextView);
                ImageView imageView = findViewById(R.id.imageView);
                Button callButton = findViewById(R.id.callButton);
                Button whatsappButton = findViewById(R.id.whatsappButton);
                ImageView favoriteButton = findViewById(R.id.favoriteImageView);
                Button deleteButton = findViewById(R.id.DeleteButton);

                if(database.canDeletePost(getSafeValue(intent.getStringExtra("Post Id")))) {
                    deleteButton.setVisibility(View.VISIBLE);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            database.deletePost(getSafeValue(intent.getStringExtra("Post Id")));
                            finish();
                        }
                    });
                }

                userNameTextView.setText("User Name: " + getSafeValue(post.get("userName")));
                phoneNumberTextView.setText("Phone Number: " + getSafeValue(post.get("phoneNumber")));
                nameTextView.setText("Title: " + getSafeValue(post.get("Name")));
                locationTextView.setText("Location: " + getSafeValue(post.get("Location")));
                streetNameTextView.setText("Street Name: " + getSafeValue(post.get("Street Name")));
                floorTextView.setText("Floor: " + getSafeValue(post.get("Floor")));
                totalFloorsTextView.setText("Total Floors: " + getSafeValue(post.get("Total Floors")));
                homeNumberTextView.setText("Home Number: " + getSafeValue(post.get("Home Number")));
                typeTextView.setText("Type: " + getSafeValue(post.get("Type")));
                viewTextView.setText("View: " + getSafeValue(post.get("View")));
                adTypeTextView.setText("Ad Type: " + getSafeValue(post.get("Ad Type")));
                additionalInformationTextView.setText("Additional Information: " + getSafeValue(post.get("Additional Information")));
                propertyCharacteristicsTextView.setText("Property Characteristics: \n" +
                        getCharacteristicsString(post));
                String photoUrl = getSafeValue(post.get("photoUrl"));
                if (photoUrl != null && !photoUrl.isEmpty()) {
                    Glide.with(this)
                            .load(photoUrl)
                            .into(imageView);
                } else {
                    imageView.setImageResource(R.drawable.picture_img);
                }


                favoriteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        favoriteClicked = !favoriteClicked;
                        if (favoriteClicked) {
                            favoriteButton.setImageResource(R.drawable.icon_favorite_filled);
                        }else{
                            favoriteButton.setImageResource(R.drawable.icon_favorite);
                        }
                        addToFavoriteDB(getSafeValue(intent.getStringExtra("Post Id")));
                    }
                });


                findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String address = getSafeValue(post.get("Location"));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
                        startActivity(mapIntent);
                    }
                });


                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToPurchasedDB(getSafeValue(intent.getStringExtra("Post Id")));
                        String phoneNumber = getSafeValue(post.get("phoneNumber"));
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        startActivity(dialIntent);
                    }
                });

                whatsappButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToPurchasedDB(getSafeValue(intent.getStringExtra("Post Id")));
                        String phoneNumber = getSafeValue(post.get("phoneNumber"));
                        Intent whatsappIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://api.whatsapp.com/send?phone=" + phoneNumber));
                        startActivity(whatsappIntent);
                    }
                });
            } else {
                Toast.makeText(this, "Error: No data passed.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error: No data passed.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getSafeValue(Object value) {
        return value != null ? value.toString() : "";
    }

    private String getCharacteristicsString(HashMap<String, Object> post) {
        StringBuilder characteristics = new StringBuilder();
        if ("true".equals(post.get("Elevators"))) {
            characteristics.append("Elevators, ");
        }
        if ("true".equals(post.get("Air Conditioner"))) {
            characteristics.append("Air conditioner, ");
        }
        if ("true".equals(post.get("Kosher Kitchen"))) {
            characteristics.append("Kosher kitchen, ");
        }
        if ("true".equals(post.get("Storage"))) {
            characteristics.append("Storage, ");
        }
        if ("true".equals(post.get("Water Heater"))) {
            characteristics.append("Water Heater, ");
        }
        if ("true".equals(post.get("Renovated"))) {
            characteristics.append("Renovated, ");
        }
        if ("true".equals(post.get("Access For Disabled"))) {
            characteristics.append("Access for disabled, ");
        }
        if ("true".equals(post.get("Furniture"))) {
            characteristics.append("Furniture, ");
        }
        return characteristics.toString();
    }
}
