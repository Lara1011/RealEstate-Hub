package com.example.realestatehub.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.realestatehub.FillDetails.ReadWritePostDetails;
import com.example.realestatehub.FillDetails.ReadWriteUserDetails;
import com.example.realestatehub.HomeFragments.ReadPost.PostAdapter;
import com.example.realestatehub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeFragment extends Fragment {

    // Declaration of variables to hold view references and Firebase data
    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList = ReadWritePostDetails.getInstance().postDetailsMap;
    private List<HashMap<String, Object>> userList;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the ProgressBar and RecyclerView
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())); // Set the layout manager
        userList = new ArrayList<>();

        // Initialize Firebase references for user and post data
        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");

        // Method call to start loading user data from Firebase
        readUserData();

        return view;
    }

    // Method to show the progress bar when loading data
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Method to hide the progress bar once data is loaded
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    // Method to read user data from Firebase
    private void readUserData() {
        showLoading(); // Show the progress bar
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HashMap<String, Object> userMap = (HashMap<String, Object>) snapshot.getValue();
                        if (userMap != null) {
                            userMap.put("id", snapshot.getKey()); // Add user ID to the map
                            userList.add(userMap); // Add the map to the list of users
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle any exceptions
                    }
                }
                readPostData(); // Once user data is loaded, start loading post data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading(); // Hide progress bar on error
                Toast.makeText(getContext(), "Failed to read user value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to read post data from Firebase
    private void readPostData() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        HashMap<String, String> postDetails = (HashMap<String, String>) postSnapshot.child("Property Details").getValue();
                        if (postDetails == null) continue; // Skip if no post details are found
                        String userId = userSnapshot.getKey();
                        // Match user ID with the post and add user details to post details
                        for (HashMap<String, Object> userMap : userList) {
                            if (userMap.get("id").equals(userId)) {
                                postDetails.put("userName", userMap.get("firstName") + " " + userMap.get("lastName"));
                                postDetails.put("phoneNumber", userMap.get("phoneNumber")+"");
                                break;
                            }
                        }
                        // Add first photo URL to post details, if available
                        DataSnapshot photosSnapshot = postSnapshot.child("Photos");
                        if (photosSnapshot.exists()) {
                            for (DataSnapshot photoSnapshot : photosSnapshot.getChildren()) {
                                postDetails.put("photoUrl", photoSnapshot.getValue(String.class));
                                break;
                            }
                        }
                        // Add the post details map to the list of posts
                        postList.put(postSnapshot.getKey(), postDetails);
                    }
                }
                hideLoading(); // Hide the progress bar after loading data
                // Create and set an adapter for the RecyclerView with the loaded post data
                PostAdapter adapter = new PostAdapter(postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading(); // Hide progress bar on error
                Toast.makeText(getContext(), "Failed to read post value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
