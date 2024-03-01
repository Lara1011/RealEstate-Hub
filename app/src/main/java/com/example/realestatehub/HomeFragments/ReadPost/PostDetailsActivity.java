package com.example.realestatehub.HomeFragments.ReadPost;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostDetailsActivity extends AppCompatActivity {
    private void addToRecentlyDB(String itemId){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserViewedItems"); // "UserViewedItems"는 최근 본 항목을 저장하는 노드 이름입니다.
        // 현재 날짜를 가져오는 코드
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> viewedItemMap = new HashMap<>();
        viewedItemMap.put("itemId", itemId);
        viewedItemMap.put("viewedDate", currentDate);
        // 업데이트할 데이터를 포함하는 Map을 생성합니다.
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(itemId, viewedItemMap);

        reference.child(uid).updateChildren(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 업데이트 성공 시 처리할 코드
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 업데이트 실패 시 처리할 코드
                    }
                });
    }
    private void addToPurchasedDB(String itemId){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("UserReachedItems"); // "UserPurchasedItems"는 최근 구매한 항목을 저장하는 노드 이름입니다.
        // 현재 날짜를 가져오는 코드
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(calendar.getTime());

        HashMap<String, Object> purchasedItemMap = new HashMap<>();
        purchasedItemMap.put("itemId", itemId);
        purchasedItemMap.put("reachedDate", currentDate);
        // 업데이트할 데이터를 포함하는 Map을 생성합니다.
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put(itemId, purchasedItemMap);

        reference.child(uid).updateChildren(updateMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // 업데이트 성공 시 처리할 코드
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 업데이트 실패 시 처리할 코드
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Intent intent = getIntent();
        if (intent != null) {
            HashMap<String, Object> post = (HashMap<String, Object>) intent.getSerializableExtra("postDetails");
            if (post != null) {
                addToRecentlyDB(  getSafeValue(intent.getStringExtra("postId")) );

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

                userNameTextView.setText("User Name: " + getSafeValue(post.get("userName")));
                phoneNumberTextView.setText("Phone Number: " + getSafeValue(post.get("phoneNumber")));
                nameTextView.setText("Title: " + getSafeValue(post.get("name")));
                locationTextView.setText("Location: " + getSafeValue(post.get("location")));
                streetNameTextView.setText("Street Name: " + getSafeValue(post.get("streetName")));
                floorTextView.setText("Floor: " + getSafeValue(post.get("floor")));
                totalFloorsTextView.setText("Total Floors: " + getSafeValue(post.get("totalFloors")));
                homeNumberTextView.setText("Home Number: " + getSafeValue(post.get("homeNumber")));
                typeTextView.setText("Type: " + getSafeValue(post.get("type")));
                viewTextView.setText("View: " + getSafeValue(post.get("view")));
                adTypeTextView.setText("Ad Type: " + getSafeValue(post.get("adType")));
                additionalInformationTextView.setText("Additional Information: " + getSafeValue(post.get("additionalInformation")));
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

                // Open Maps when address is clicked
                findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String address = getSafeValue(post.get("location"));
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
                        startActivity(mapIntent);
                    }
                });

                // Open Dialer when phone number is clicked
                callButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToPurchasedDB(  getSafeValue(intent.getStringExtra("postId")) );
                        String phoneNumber = getSafeValue(post.get("phoneNumber"));
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        startActivity(dialIntent);
                    }
                });

                // Open WhatsApp when WhatsApp button is clicked
                whatsappButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        addToPurchasedDB(  getSafeValue(intent.getStringExtra("postId")) );
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
        if ("true".equals(post.get("elevators"))) {
            characteristics.append("Elevators, ");
        }
        if ("true".equals(post.get("airConditioner"))) {
            characteristics.append("Tornado air conditioner, ");
        }
        if ("true".equals(post.get("kosherKitchen"))) {
            characteristics.append("Kosher kitchen, ");
        }
        if ("true".equals(post.get("storage"))) {
            characteristics.append("Storage, ");
        }
        if ("true".equals(post.get("waterHeater"))) {
            characteristics.append("Water Heater, ");
        }
        if ("true".equals(post.get("renovated"))) {
            characteristics.append("Renovated, ");
        }
        if ("true".equals(post.get("accessForDisabled"))) {
            characteristics.append("Access for disabled, ");
        }
        if ("true".equals(post.get("furniture"))) {
            characteristics.append("Furniture, ");
        }
        return characteristics.toString();
    }
}
