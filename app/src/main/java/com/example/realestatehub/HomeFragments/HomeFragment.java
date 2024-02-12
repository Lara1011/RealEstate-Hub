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

    private RecyclerView recyclerView;
    private List<HashMap<String, Object>> postList;
    private List<HashMap<String, Object>> userList;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progressBar);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        postList = new ArrayList<>();
        userList = new ArrayList<>();

        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");

        readUserData();

        return view;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    private void readUserData() {
        showLoading();
        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HashMap<String, Object> userMap = (HashMap<String, Object>) snapshot.getValue();
                        if (userMap != null) {
                            userMap.put("id", snapshot.getKey());
                            userList.add(userMap);
                        } else {
                            // Handle null user data
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                readPostData();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading();
                Toast.makeText(getContext(), "Failed to read user value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readPostData() {
        postsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        HashMap<String, Object> postDetails = (HashMap<String, Object>) postSnapshot.child("Property Details").getValue();
                        if (postDetails == null) continue;
                        String userId = userSnapshot.getKey();
                        for (HashMap<String, Object> userMap : userList) {
                            if (userMap.get("id").equals(userId)) {
                                postDetails.put("userName", userMap.get("firstName") + " " + userMap.get("lastName"));
                                postDetails.put("phoneNumber", userMap.get("phoneNumber"));
                                break;
                            }
                        }
                        DataSnapshot photosSnapshot = postSnapshot.child("Photos");
                        if (photosSnapshot.exists()) {
                            for (DataSnapshot photoSnapshot : photosSnapshot.getChildren()) {
                                postDetails.put("photoUrl", photoSnapshot.getValue(String.class));
                                break;
                            }
                        }
                        postList.add(postDetails);
                    }
                }
                hideLoading();
                PostAdapter adapter = new PostAdapter(postList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading();
                Toast.makeText(getContext(), "Failed to read post value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
