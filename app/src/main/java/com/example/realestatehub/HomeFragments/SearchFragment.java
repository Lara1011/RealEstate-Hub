package com.example.realestatehub.HomeFragments;

import android.app.Dialog;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.realestatehub.HomeFragments.ReadPost.PostAdapter;
import com.example.realestatehub.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SearchFragment extends Fragment implements View.OnClickListener {
   private View view;
    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList = new HashMap<>();
    private HashMap<String, HashMap<String, String>> filteredList = new HashMap<>();
    private HashSet<String> selectedFilters = new HashSet<>();
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
    private Button resetButton;

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

//------------------------------------------------------------------------------------------------
//----------------------------------------F I L T E R I N G---------------------------------------
//------------------------------------------------------------------------------------------------
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
        resetButton = dialog.findViewById(R.id.resetButton);

        continueButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
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
        int id = v.getId();
        if (id == R.id.filterTextView) {
            showBottomDialog();
        } else if (id == R.id.rentLinearLayout || id == R.id.buyLinearLayout || id == R.id.allLinearLayout || id == R.id.apartmentLinearLayout || id == R.id.buildingLinearLayout || id == R.id.houseLinearLayout || id == R.id.parkingLinearLayout || id == R.id.penthouseLinearLayout || id == R.id.loftLinearLayout || id == R.id.storageLinearLayout) {
            toggleFilterSelection(v);
        } else if (id == R.id.continueButton) {
            applyFilters();
        } else if (id == R.id.resetButton) {
            resetFilters();
        }
    }

    private void toggleFilterSelection(View view) {
        LinearLayout layout = (LinearLayout) view;
        TextView textView = layout.findViewById(getTextViewIdForLayoutId(view.getId()));
        String filter = textView.getText().toString();

        if (selectedFilters.contains(filter)) {
            selectedFilters.remove(filter);
            changeFrame(layout, textView, false);
        } else {
            selectedFilters.add(filter);
            changeFrame(layout, textView, true);
        }
    }

    private void changeFrame(LinearLayout layout, TextView textView, boolean isSelected) {
        if (isSelected) {
            // Set filled background and white text
            layout.setBackgroundResource(R.drawable.background_filled_frame_blue);
            textView.setTextColor(Color.WHITE);
        } else {
            // Set unfilled background and blue text
            layout.setBackgroundResource(R.drawable.background_empty_frame_blue);
            textView.setTextColor(Color.BLUE);
        }
    }

    private int getTextViewIdForLayoutId(int layoutId) {
        if (layoutId == R.id.rentLinearLayout) {
            return R.id.rentTextView;
        } else if (layoutId == R.id.buyLinearLayout) {
            return R.id.buyTextView;
        } else if (layoutId == R.id.allLinearLayout) {
            return R.id.allTextView;
        } else if (layoutId == R.id.apartmentLinearLayout) {
            return R.id.apartmentTextView;
        } else if (layoutId == R.id.buildingLinearLayout) {
            return R.id.buildingTextView;
        } else if (layoutId == R.id.houseLinearLayout) {
            return R.id.houseTextView;
        } else if (layoutId == R.id.parkingLinearLayout) {
            return R.id.parkingTextView;
        } else if (layoutId == R.id.penthouseLinearLayout) {
            return R.id.penthouseTextView;
        } else if (layoutId == R.id.loftLinearLayout) {
            return R.id.loftTextView;
        } else if (layoutId == R.id.storageLinearLayout) {
            return R.id.storageTextView;
        }
        return -1;
    }

    private void applyFilters() {
        filteredList.clear();
        int i = 0;
        for (HashMap<String, String> post : postList.values()) {
            for (String filter : selectedFilters) {
                if (post.get("type").toLowerCase().contains(filter.toLowerCase())) {
                    filteredList.put(String.valueOf(i++), post);
                    break; // Move to the next post once a match is found
                }
            }
        }
        if (filteredList.isEmpty()) {
            Toast.makeText(getContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
        PostAdapter adapter = new PostAdapter(filteredList);
        recyclerView.setAdapter(adapter);
        dialog.dismiss();
    }

    private void resetFilters() {
        for (LinearLayout layout : getFilterLayouts()) {
            TextView textView = layout.findViewById(getTextViewIdForLayoutId(layout.getId()));
            changeFrame(layout, textView, false);
        }
        selectedFilters.clear();
    }

    private List<LinearLayout> getFilterLayouts() {
        List<LinearLayout> layouts = new ArrayList<>();
        layouts.add(rentLayout);
        layouts.add(buyLayout);
        layouts.add(allLayout);
        layouts.add(apartmentLayout);
        layouts.add(buildingLayout);
        layouts.add(houseLayout);
        layouts.add(parkingLayout);
        layouts.add(penthouseLayout);
        layouts.add(loftLayout);
        layouts.add(storageLayout);
        return layouts;
    }
}