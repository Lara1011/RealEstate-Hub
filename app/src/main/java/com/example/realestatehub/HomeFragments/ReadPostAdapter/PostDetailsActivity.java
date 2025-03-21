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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class PostDetailsActivity extends AppCompatActivity {
    private Database database;
    private boolean favoriteClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        database = Database.getInstance(this);
        Intent intent = getIntent();
        if (intent != null) {
            String userId = intent.getStringExtra("User Id"); // Post owner ID
            String postId = intent.getStringExtra("Post Id").replace(" ", ""); // Post ID
            String postName = intent.getStringExtra("Post Name"); // Post ID
            HashMap<String, Object> post = (HashMap<String, Object>) intent.getSerializableExtra("Post Details");
            if (post != null) {
                // Increment views counter and add to recently viewed
                String currentUser = database.getFirebaseUser().getUid();
                if (!currentUser.equals(userId))
                    incrementViewsCounter(userId, postId, postName);
                addToRecentlyDB(postId);
                TextView priceTextView = findViewById(R.id.priceTextView);
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
                TextView likesTextView = findViewById(R.id.likesTextView);
                TextView viewsTextView = findViewById(R.id.viewsTextView);
                ImageView imageView = findViewById(R.id.imageView);
                Button callButton = findViewById(R.id.callButton);
                Button whatsappButton = findViewById(R.id.whatsappButton);
                ImageView favoriteButton = findViewById(R.id.favoriteImageView);
                Button deleteButton = findViewById(R.id.DeleteButton);

                if (database.getReadWriteUserDetails().getPurpose().contains("Seller")) {
                    database.canDeletePost(getSafeValue(intent.getStringExtra("Post Id")), new Database.OnCheckCompleteListener() {
                        @Override
                        public void onCheckComplete(boolean canDelete) {
                            if (canDelete) {
                                likesTextView.setVisibility(View.VISIBLE);
                                viewsTextView.setVisibility(View.VISIBLE);
                                deleteButton.setVisibility(View.VISIBLE);
                                deleteButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        database.deletePost(getSafeValue(intent.getStringExtra("Post Id")), new Database.GeneralCallback() {
                                            @Override
                                            public void onSuccess() {
                                                Toast.makeText(PostDetailsActivity.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            @Override
                                            public void onFailure(int errorCode, String errorMessage) {
                                                if (errorCode == 0) {
                                                    Toast.makeText(PostDetailsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });
                }

                priceTextView.setText("Price: " + getSafeValue(post.get("Price")));
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
                likesTextView.setText("Total Likes: " + getSafeValue(post.get("likes")));
                viewsTextView.setText("Total Views: " + getSafeValue(post.get("views")));
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


                favoriteButton.setOnClickListener(view -> {
                    favoriteClicked = !favoriteClicked;
                    if (favoriteClicked) {
                        int count = Integer.parseInt(likesTextView.getText().toString().substring(13));
                        likesTextView.setText("Total Likes: " + (count + 1));
                        favoriteButton.setImageResource(R.drawable.icon_favorite_filled);

                        // Add to Favorites
                        addToFavoriteDB(userId, postId, post);
                        incrementLikesCounter(userId, postId, postName);

                    } else {
                        int count = Integer.parseInt(likesTextView.getText().toString().substring(13));
                        if (count != 0)
                            likesTextView.setText("Total Likes: " + (count - 1));
                        favoriteButton.setImageResource(R.drawable.icon_favorite);

                        removeFromFavoriteDB(postId);
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

    private void addToFavoriteDB(String userId, String postId, HashMap<String, Object> postDetails) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Favorites").child(uid);

        HashMap<String, Object> favoriteData = new HashMap<>(postDetails);
        favoriteData.put("User Id", userId);
        favoriteData.put("Post Id", postId);

        reference.child(postId).setValue(favoriteData)
                .addOnSuccessListener(aVoid -> Toast.makeText(PostDetailsActivity.this, "Added to Favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PostDetailsActivity.this, "Failed to Add to Favorites", Toast.LENGTH_SHORT).show());
    }

    private void removeFromFavoriteDB(String postId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Favorites").child(uid);

        reference.child(postId).removeValue()
                .addOnSuccessListener(aVoid -> Toast.makeText(PostDetailsActivity.this, "Removed from Favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(PostDetailsActivity.this, "Failed to Remove from Favorites", Toast.LENGTH_SHORT).show());
    }

    private void incrementLikesCounter(String userId, String postId, String postTitle) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Users Posts")
                .child(userId).child(postId);

        postRef.child("likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull com.google.firebase.database.DataSnapshot snapshot) {
                long currentLikes = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
                postRef.child("likes").setValue(currentLikes + 1);

                String message = "Someone just liked your post: " + postTitle;
                database.sendNotificationToSeller(userId, message);
            }

            @Override
            public void onCancelled(@NonNull com.google.firebase.database.DatabaseError error) {
                Toast.makeText(PostDetailsActivity.this, "Failed to update likes counter", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void incrementViewsCounter(String userId, String postId, String postTitle) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Users Posts")
                .child(userId).child(postId);

        postRef.child("views").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long currentViews = snapshot.exists() ? snapshot.getValue(Long.class) : 0;
                postRef.child("views").setValue(currentViews + 1);

                String message = "Someone just viewed your post: " + postTitle;
                database.sendNotificationToSeller(userId, message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PostDetailsActivity.this, "Failed to update views counter", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void addToRecentlyDB(String itemId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Recently Viewed");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> viewedItemMap = new HashMap<>();
        viewedItemMap.put("Post Id", itemId);
        viewedItemMap.put("Date", currentDate);

        reference.child(uid).child(itemId).setValue(viewedItemMap);
    }

    private void addToPurchasedDB(String itemId) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User Recently Reached");
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> purchasedItemMap = new HashMap<>();
        purchasedItemMap.put("Post Id", itemId);
        purchasedItemMap.put("Date", currentDate);

        reference.child(uid).child(itemId).setValue(purchasedItemMap);
    }
}
