package com.example.realestatehub.HomeFragments.ProfileFragmentLayouts;

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
import com.example.realestatehub.Utils.ReadWritePostDetails;
import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecentlyViewedFragment extends Fragment {

    private RecyclerView recyclerView;
    private HashMap<String, HashMap<String, String>> postList = ReadWritePostDetails.getInstance().postDetailsMap;
    private List<HashMap<String, Object>> userList;
    private DatabaseReference usersReference;
    private DatabaseReference postsReference;
    private DatabaseReference viewedItemsReference;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_viewed, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        userList = new ArrayList<>();
        usersReference = FirebaseDatabase.getInstance().getReference("Registered Users");
        postsReference = FirebaseDatabase.getInstance().getReference("Users Posts");
        viewedItemsReference = FirebaseDatabase.getInstance().getReference("UserViewedItems"); // "UserViewedItems"는 최근 본 항목을 저장하는 노드 이름입니다.
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
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateRecentViewedItems();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                hideLoading();
                Toast.makeText(getContext(), "Failed to read user value: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecentViewedItems() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewedItemsReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    List<HashMap<String, String>> viewedItems = new ArrayList<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> viewedItem = new HashMap<>();
                        viewedItem.put("itemId", snapshot.child("itemId").getValue(String.class));
                        viewedItem.put("viewedDate", snapshot.child("viewedDate").getValue(String.class)); // viewedDate 추가
                        viewedItems.add(viewedItem);
                    }
                    displayRecentViewedPosts(viewedItems);
                } else {
                    hideLoading();
                    Toast.makeText(getContext(), "No recently viewed items.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                hideLoading();
                Toast.makeText(getContext(), "Failed to read recently viewed items: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecentViewedPosts(final List<HashMap<String, String>> viewedItems) {
        final HashMap<String, HashMap<String, String>> recentViewedPosts = new HashMap<>();
        postsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot postSnapshot : userSnapshot.getChildren()) {
                        String postId = postSnapshot.getKey();
                        for (HashMap<String, String> viewedItem : viewedItems) {
                            if (postId.equals(viewedItem.get("itemId"))) {
                                HashMap<String, String> postDetails = (HashMap<String, String>) postSnapshot.child("Property Details").getValue();
                                if (postDetails != null) {
                                    String userId = userSnapshot.getKey();
                                    for (HashMap<String, Object> userMap : userList) {
                                        if (userMap.get("id").equals(userId)) {
                                            postDetails.put("userName", userMap.get("firstName") + " " + userMap.get("lastName"));
                                            postDetails.put("phoneNumber", userMap.get("phoneNumber") + "");
                                            postDetails.put("viewedDate", viewedItem.get("viewedDate")); // viewedDate 추가
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
                                    recentViewedPosts.put(postId, postDetails);
                                }
                            }
                        }
                    }
                }
                showRecentViewedPosts(recentViewedPosts);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                hideLoading();
                Toast.makeText(getContext(), "Failed to read post value: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRecentViewedPosts(HashMap<String, HashMap<String, String>> recentViewedPosts) {

        List<HashMap.Entry<String, HashMap<String, String>>> list = new ArrayList<>(recentViewedPosts.entrySet());
        Collections.sort(list, new Comparator<HashMap.Entry<String, HashMap<String, String>>>() {
            @Override
            public int compare(HashMap.Entry<String, HashMap<String, String>> o1, HashMap.Entry<String, HashMap<String, String>> o2) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    Date date1 = dateFormat.parse(o1.getValue().get("viewedDate"));
                    Date date2 = dateFormat.parse(o2.getValue().get("viewedDate"));
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });

        HashMap<String, HashMap<String, String>> sortedRecentViewedPosts = new HashMap<>();
        for (HashMap.Entry<String, HashMap<String, String>> entry : list) {
            sortedRecentViewedPosts.put(entry.getKey(), entry.getValue());
        }

        hideLoading();
        PostAdapter adapter = new PostAdapter(sortedRecentViewedPosts);
        recyclerView.setAdapter(adapter);
    }
}
