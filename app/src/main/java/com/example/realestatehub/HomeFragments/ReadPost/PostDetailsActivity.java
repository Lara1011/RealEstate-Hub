package com.example.realestatehub.HomeFragments.ReadPost;
// Import necessary Android classes, Glide for image loading, and other utilities.
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

import java.io.Serializable;
// Declare the class extending AppCompatActivity to make it an activity.
public class PostDetailsActivity extends AppCompatActivity {
    // Override the onCreate method to set up the activity's UI and functionality.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set the content view to the layout defined for post details.
        setContentView(R.layout.activity_post_details);
        // Retrieve the intent that started this activity to get the PostDetails object.
        Intent intent = getIntent();
        if (intent != null) {
            PostDetails postDetails = (PostDetails) intent.getSerializableExtra("postDetails");
            // Find and initialize UI components to display the post details.
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
            // More TextViews and an ImageView for other details and the photo.
            ImageView imageView = findViewById(R.id.imageView);
            Button callButton = findViewById(R.id.callButton);
            Button whatsappButton = findViewById(R.id.whatsappButton);
            // Populate the UI components with the data from the PostDetails object.
            // This includes setting text for TextViews and loading the image with Glide.
            if (postDetails.userName != null)
                userNameTextView.setText("User Name: " +  postDetails.userName  );
            if (postDetails.phoneNumber != null)
                phoneNumberTextView.setText("Phone Number: " + postDetails.phoneNumber);
            // Similar assignments for other TextViews to display property details.
            // Check for null or empty fields where necessary, especially for the image URL.
            nameTextView.setText("Title: " + postDetails.getName());
            locationTextView.setText("Location: " + postDetails.getLocation());
            streetNameTextView.setText("Street Name: " + postDetails.getStreetName());
            floorTextView.setText("Floor: " + postDetails.getFloor());
            totalFloorsTextView.setText("Total Floors: " + postDetails.getTotalFloors());
            homeNumberTextView.setText("Home Number: " + postDetails.getHomeNumber());
            typeTextView.setText("Type: " + postDetails.getType());
            viewTextView.setText("View: " + postDetails.getView());
            adTypeTextView.setText("Ad Type: " + postDetails.getAdType());
            additionalInformationTextView.setText("Additional Information: " + postDetails.getAdditionalInformation());
            propertyCharacteristicsTextView.setText("Property Characteristics: \n" +
                   ( postDetails.elevators.equals("true") == true ? "Elevators, " : "")
                    + (postDetails.airConditioner.equals("true") == true? "Tornado air conditioner, " : "")
                    + (postDetails.kosherKitchen.equals("true") == true? "Kosher kitchen, " : "")
                    + (postDetails.storage.equals("true") == true? "Storage, " : "")
                    + (postDetails.waterHeater.equals("true")== true ? "Water Heater, " : "")
                    +( postDetails.renovated.equals("true")== true ? "Renovated, " : "")
                    + (postDetails.accessForDisabled.equals("true")== true ? "Access for disabled, " : "")
                    + (postDetails.furniture.equals("true")== true ? "Furniture " : ""));


            if (postDetails.getPhotoUrl() != null && !postDetails.getPhotoUrl().isEmpty()) {
                Glide.with(this)
                        .load(postDetails.getPhotoUrl())
                        .into(imageView);
            } else {
                 imageView.setImageResource(R.drawable.picture_img);
            }
            // Implement additional functionality, such as opening a map when the location is clicked.
            findViewById(R.id.locationButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String address = postDetails.getLocation();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + address));
                    startActivity(intent);
                }
            });

            // Implement functionality to open the dialer with the phone number when the call button is clicked.
            callButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phoneNumber = postDetails.getPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + phoneNumber));
                    startActivity(intent);
                }
            });

            // Implement functionality to open WhatsApp with the given phone number when the WhatsApp button is clicked.
            whatsappButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phoneNumber = postDetails.getPhoneNumber();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + phoneNumber));
                    startActivity(intent);
                }
            });

        } else {
            // Show a toast message if no data was passed to this activity.
            Toast.makeText(this, "Error: No data passed.", Toast.LENGTH_SHORT).show();
        }
    }
}
