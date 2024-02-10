package com.example.realestatehub.HomeFragments;

// Import necessary Android, Firebase, and application classes.
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
import com.example.realestatehub.HomeFragments.ReadPost.PostAdapter;
import com.example.realestatehub.HomeFragments.ReadPost.PostDetails;
import com.example.realestatehub.HomeFragments.ReadPost.User;
import com.example.realestatehub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

// Define HomeFragment class which extends Fragment to display a list of posts.
public class HomeFragment extends Fragment {

    // Declare variables for the RecyclerView, list of posts, list of users, Firebase references, and ProgressBar.
    private RecyclerView recyclerView;
    private List<PostDetails> postList;
    private List<User> userList;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private ProgressBar progressBar;

    // onCreateView method to inflate the layout for this fragment.
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment.
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize the ProgressBar and RecyclerView.
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // Initialize lists for storing user and post data.
        postList = new ArrayList<>();
        userList = new ArrayList<>();

        // Reference Firebase database paths for users and posts.
        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");

        // Method call to start loading and displaying user data.
        readUserData();

        return view; // Return the inflated view.
    }

    // Method to show the loading indicator.
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Method to hide the loading indicator.
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    // Method to read user data from Firebase.
    private void readUserData() {
        showLoading(); // Show the progress bar.
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear(); // Clear the existing list to avoid duplicates.
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        User user = snapshot.getValue(User.class); // Convert snapshot to User class.
                        if (user != null) {
                            user.id = snapshot.getKey(); // Set the user's ID.
                            userList.add(user); // Add the user to the list.
                        } else {
                            // Optionally handle null user data.
                        }
                    } catch (Exception e) {
                        e.printStackTrace(); // Handle exceptions.
                    }
                }
                readPostData(); // After reading users, start reading posts.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors and hide the loading indicator.
                hideLoading();
                Toast.makeText(getContext(), "Failed to read user value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to read post data from Firebase, linking each post with its user.
    private void readPostData() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear(); // Clear the existing list to avoid duplicates.
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) { // Iterate over each user.
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) { // Iterate over each post.
                        // Extract post details and link them to their respective user.
                        PostDetails postDetails = postSnapshot.child("Property Details").getValue(PostDetails.class);
                        if (postDetails == null) continue;
                        String userId = userSnapshot.getKey();
                        for (User user : userList) { // Find and set the user's name and phone number in each post.
                            if (user.id.equals(userId)) {
                                postDetails.userName = user.firstName + " " + user.lastName;
                                postDetails.phoneNumber = user.phoneNumber;
                                break;
                            }
                        }
                        // Extract the first photo URL for the post.
                        DataSnapshot photosSnapshot = postSnapshot.child("Photos");
                        if (photosSnapshot.exists()) {
                            for (DataSnapshot photoSnapshot : photosSnapshot.getChildren()) {
                                postDetails.setPhotoUrl(photoSnapshot.getValue(String.class));
                                break;
                            }
                        }
                        postList.add(postDetails); // Add the post details to the list.
                    }
                }
                // After fetching and preparing the data, update the UI.
                hideLoading();
                PostAdapter adapter = new PostAdapter(postList);
                recyclerView.setAdapter(adapter); // Set the adapter to the RecyclerView.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors and hide the loading indicator.
                hideLoading();
                Toast.makeText(getContext(), "Failed to read post value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
