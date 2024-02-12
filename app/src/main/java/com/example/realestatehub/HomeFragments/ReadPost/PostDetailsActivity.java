package com.example.realestatehub.HomeFragments.ReadPost;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.realestatehub.R;

import java.util.HashMap;

public class PostDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);

        Intent intent = getIntent();
        if (intent != null) {
            HashMap<String, Object> post = (HashMap<String, Object>) intent.getSerializableExtra("postDetails");
            if (post != null) {
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
                        String phoneNumber = getSafeValue(post.get("phoneNumber"));
                        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber));
                        startActivity(dialIntent);
                    }
                });

                // Open WhatsApp when WhatsApp button is clicked
                whatsappButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
