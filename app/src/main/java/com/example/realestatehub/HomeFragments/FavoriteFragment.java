package com.example.realestatehub.HomeFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FavoriteFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList;
    private List<String> favoriteMap;
    private List<HashMap<String, Object>> userList;
    private DatabaseReference usersReference, postsReference, favoritesReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        initUI();
        readUserData();

        return view;
    }

    private void initUI() {
        postList = new HashMap<>();
        userList = new ArrayList<>();
        favoriteMap = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");
        favoritesReference = FirebaseDatabase.getInstance().getReference("Users Favorites");

    }
    private void updateFavoriteData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        favoritesReference.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    try {
                        HashMap<String, String> favMap = (HashMap<String, String>) snapshot.getValue();
                        if (favMap != null) {
                            favoriteMap.add(favMap.get("Post Id"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                readPostData();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                Toast.makeText(getContext(), "Failed to read recently viewed items: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void readUserData() {
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateFavoriteData();
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
                postList.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        HashMap<String, String> postDetails = new HashMap<>((Map<String, String>) postSnapshot.child("Property Details").getValue());
                        if (postDetails.isEmpty()) {
                            continue;
                        }
                        String postId = postSnapshot.getKey();
                        String userId = userSnapshot.getKey();
                        if(!favoriteMap.contains(postId)){
                            continue;
                        }
                        for (HashMap<String, Object> userMap : userList) {
                            if (Objects.equals(userMap.get("id"), userId)) {
                                postDetails.put("userName", userMap.get("firstName") + " " + userMap.get("lastName"));
                                postDetails.put("phoneNumber", String.valueOf(userMap.get("phoneNumber")));
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