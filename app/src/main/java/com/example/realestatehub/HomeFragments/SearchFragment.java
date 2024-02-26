package com.example.realestatehub.HomeFragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
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

public class SearchFragment extends Fragment implements View.OnClickListener {
    private String selectedFilter = "all";
    private View view;
    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList = new HashMap<>();
    private HashMap<String, HashMap<String, String>> filteredList = new HashMap<>();
    private List<HashMap<String, Object>> userList;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private SearchView searchView;
    private TextView filterTextView;
    private LinearLayout rentLayout;
    private LinearLayout buyLayout;
    private LinearLayout allLayout;
    private LinearLayout apartmentLayout;
    private LinearLayout buildingLayout;
    private LinearLayout houseLayout;
    private LinearLayout parkingLayout;
    private LinearLayout penthouseLayout;
    private LinearLayout loftLayout;
    private LinearLayout storageLayout;
    private Dialog dialog;
    private Button continueButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);
        searchView = view.findViewById(R.id.searchEditText);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        userList = new ArrayList<>();
        filterTextView = view.findViewById(R.id.filterTextView);
        filterTextView.setOnClickListener(this);

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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to read post value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showBottomDialog() {
        dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_search_filter);

        rentLayout = dialog.findViewById(R.id.rentLinearLayout);
        buyLayout = dialog.findViewById(R.id.buyLinearLayout);
        allLayout = dialog.findViewById(R.id.allLinearLayout);
        apartmentLayout = dialog.findViewById(R.id.apartmentLinearLayout);
        buildingLayout = dialog.findViewById(R.id.buildingLinearLayout);
        houseLayout = dialog.findViewById(R.id.houseLinearLayout);
        parkingLayout = dialog.findViewById(R.id.parkingLinearLayout);
        penthouseLayout = dialog.findViewById(R.id.penthouseLinearLayout);
        loftLayout = dialog.findViewById(R.id.loftLinearLayout);
        storageLayout = dialog.findViewById(R.id.storageLinearLayout);

        continueButton = dialog.findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        rentLayout.setOnClickListener(this);
        buyLayout.setOnClickListener(this);
        allLayout.setOnClickListener(this);
        apartmentLayout.setOnClickListener(this);
        buildingLayout.setOnClickListener(this);
        houseLayout.setOnClickListener(this);
        parkingLayout.setOnClickListener(this);
        penthouseLayout.setOnClickListener(this);
        loftLayout.setOnClickListener(this);
        storageLayout.setOnClickListener(this);

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.filterTextView) {
            showBottomDialog();
        } else if (v.getId() == R.id.rentLinearLayout) {
            changeFrame(rentLayout, dialog.findViewById(R.id.rentTextView));
            Toast.makeText(getContext(), "Rent", Toast.LENGTH_SHORT).show();
            selectedFilter = "Rent";
        } else if (v.getId() == R.id.buyLinearLayout) {
            changeFrame(buyLayout, dialog.findViewById(R.id.buyTextView));
            Toast.makeText(getContext(), "Buy", Toast.LENGTH_SHORT).show();
            selectedFilter = "Buy";
        } else if (v.getId() == R.id.allLinearLayout) {
            changeFrame(allLayout, dialog.findViewById(R.id.allTextView));
            Toast.makeText(getContext(), "All", Toast.LENGTH_SHORT).show();
            selectedFilter = "All";
        } else if (v.getId() == R.id.apartmentLinearLayout) {
            changeFrame(apartmentLayout, dialog.findViewById(R.id.apartmentTextView));
            Toast.makeText(getContext(), "Apartment", Toast.LENGTH_SHORT).show();
            selectedFilter = "Apartment";
        } else if (v.getId() == R.id.buildingLinearLayout) {
            changeFrame(buildingLayout, dialog.findViewById(R.id.buildingTextView));
            Toast.makeText(getContext(), "Building", Toast.LENGTH_SHORT).show();
            selectedFilter = "Building";
        } else if (v.getId() == R.id.houseLinearLayout) {
            changeFrame(houseLayout, dialog.findViewById(R.id.houseTextView));
            Toast.makeText(getContext(), "House", Toast.LENGTH_SHORT).show();
            selectedFilter = "House";
        } else if (v.getId() == R.id.parkingLinearLayout) {
            changeFrame(parkingLayout, dialog.findViewById(R.id.parkingTextView));
            Toast.makeText(getContext(), "Parking", Toast.LENGTH_SHORT).show();
            selectedFilter = "Parking";
        } else if (v.getId() == R.id.penthouseLinearLayout) {
            changeFrame(penthouseLayout, dialog.findViewById(R.id.penthouseTextView));
            Toast.makeText(getContext(), "Penthouse", Toast.LENGTH_SHORT).show();
            selectedFilter = "Penthouse";
        } else if (v.getId() == R.id.loftLinearLayout) {
            changeFrame(loftLayout, dialog.findViewById(R.id.loftTextView));
            Toast.makeText(getContext(), "Loft", Toast.LENGTH_SHORT).show();
            selectedFilter = "Loft";
        } else if (v.getId() == R.id.storageLinearLayout) {
            changeFrame(storageLayout, dialog.findViewById(R.id.storageTextView));
            Toast.makeText(getContext(), "Storage", Toast.LENGTH_SHORT).show();
            selectedFilter = "Storage";
        } else if (v.getId() == R.id.continueButton) {
            filterList(selectedFilter);
            dialog.dismiss();
        }
    }

    private void changeFrame(LinearLayout layout, TextView textView) {
        if (layout.getTag() == null || !((boolean) layout.getTag())) {
            // Set filled background and white text
            layout.setBackgroundResource(R.drawable.filled_blue_frame_textview);
            textView.setTextColor(Color.WHITE);
            layout.setTag(true);
            Toast.makeText(getContext(), "Rent", Toast.LENGTH_SHORT).show();
        } else {
            // Set unfilled background and blue text
            layout.setBackgroundResource(R.drawable.empty_blue_frame_textview);
            textView.setTextColor(Color.BLUE);
            layout.setTag(false);
        }
    }

    private void filterList(String status) {
        selectedFilter = status;
        filteredList.clear();
        int i = 0;
        for (HashMap<String, String> post : postList.values()) {
            if (post.get("type").toLowerCase().contains(status.toLowerCase())) {
                filteredList.put(String.valueOf(i++), post);
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
        PostAdapter adapter = new PostAdapter(filteredList);
        recyclerView.setAdapter(adapter);
    }
}
