package com.example.realestatehub.HomeFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.realestatehub.FillDetails.ReadWritePostDetails;
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
import java.util.Map;

public class SearchFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList = new HashMap<>();
    private HashMap<String, HashMap<String, String>> filteredList = new HashMap<>();
    private List<HashMap<String, Object>> userList;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        userList = new ArrayList<>();

        // Initialize Firebase references for user and post data
        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");

        // Method call to start loading user data from Firebase
        readUserData();

        // Call initSearch after initializing RecyclerView
        initSearch();

        return view;
    }

    private void initSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Apply search filter when text changes
                String searchText = newText.trim().toLowerCase(); // Trim and convert to lowercase
                applySearchFilter(searchText);
                return true;
            }
        });
    }

    private void applySearchFilter(String query) {
        filteredList.clear();
        int i = 0;
        for (HashMap<String, String> post : postList.values()) {
            if (post.get("name").toLowerCase().contains(query.toLowerCase())) {
                filteredList.put(String.valueOf(i++), post);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
        PostAdapter adapter = new PostAdapter(filteredList);
        recyclerView.setAdapter(adapter);
    }

    private void readUserData() {
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
                Toast.makeText(getContext(), "Failed to read user value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readPostData() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear(); // Clear existing data to avoid duplicates
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        HashMap<String, String> postDetails = new HashMap<>();
                        postDetails.putAll((Map<String, String>) postSnapshot.child("Property Details").getValue());
                        if (postDetails.isEmpty()) continue; // Skip if no post details are found
                        String postId = postSnapshot.getKey(); // Unique key for each post
                        String userId = userSnapshot.getKey();
                        // Match user ID with the post and add user details to post details
                        for (HashMap<String, Object> userMap : userList) {
                            if (userMap.get("id").equals(userId)) {
                                postDetails.put("userName", userMap.get("firstName") + " " + userMap.get("lastName"));
                                postDetails.put("phoneNumber", userMap.get("phoneNumber") + "");
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
                        // Add the post details map to the list of posts using postId as key
                        postList.put(postId, postDetails);
                    }
                }
                PostAdapter adapter = new PostAdapter(postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to read post value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
