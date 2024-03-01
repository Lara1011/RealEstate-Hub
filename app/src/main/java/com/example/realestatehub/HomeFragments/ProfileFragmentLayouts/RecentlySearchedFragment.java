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
import com.example.realestatehub.FillDetails.ReadWritePostDetails;
import com.example.realestatehub.HomeFragments.ReadPost.PostAdapter;
import com.example.realestatehub.HomeFragments.ReadPost.SearchAdapter;
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

public class RecentlySearchedFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference searchItemsReference;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_searched, container, false);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        searchItemsReference = FirebaseDatabase.getInstance().getReference("UserSearchItems"); // "UserSearchItems"는 최근 검색한 항목을 저장하는 노드 이름입니다.
        updateRecentSearchItems();
        return view;
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }


    private void updateRecentSearchItems() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        searchItemsReference.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    HashMap<String, HashMap<String, String>> searchItems = new HashMap<>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> searchItemDetails = new HashMap<>();
                        String itemId = snapshot.child("itemId").getValue(String.class); // itemId를 키로 사용
                        String filter = snapshot.child("filter").getValue(String.class);
                        searchItemDetails.put("searchDate", snapshot.child("searchDate").getValue(String.class));
                        searchItemDetails.put("keyword", snapshot.child("keyword").getValue(String.class));
                        searchItemDetails.put("filter", snapshot.child("filter").getValue(String.class));

                        searchItems.put(itemId+filter, searchItemDetails); // itemId를 키로 하여 세부 정보 맵 추가
                    }
                    showRecentSearchPosts(searchItems);

                } else {
                    hideLoading();
                    Toast.makeText(getContext(), "No recent search items.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                hideLoading();
                Toast.makeText(getContext(), "Failed to read recent search items: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRecentSearchPosts( HashMap<String, HashMap<String, String>> recentSearchPosts) {

        List<HashMap.Entry<String, HashMap<String, String>>> list = new ArrayList<>(recentSearchPosts.entrySet());
        Collections.sort(list, new Comparator<HashMap.Entry<String, HashMap<String, String>>>() {
            @Override
            public int compare(HashMap.Entry<String, HashMap<String, String>> o1, HashMap.Entry<String, HashMap<String, String>> o2) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    Date date1 = dateFormat.parse(o1.getValue().get("searchDate"));
                    Date date2 = dateFormat.parse(o2.getValue().get("searchDate"));
                    return date2.compareTo(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });


        HashMap<String, HashMap<String, String>> sortedRecentSearchPosts = new HashMap<>();
        for (HashMap.Entry<String, HashMap<String, String>> entry : list) {
            sortedRecentSearchPosts.put(entry.getKey(), entry.getValue());
        }

        hideLoading();
        SearchAdapter adapter = new SearchAdapter(sortedRecentSearchPosts);
        recyclerView.setAdapter(adapter);
    }
}
