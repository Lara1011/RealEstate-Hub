package com.example.realestatehub.HomeFragments.ProfileFragmentLayouts;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestatehub.Utils.Database;
import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecentlyViewedFragment extends Fragment {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_viewed, container, false);

        database = new Database(getContext());
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
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
        database.readUsersData(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                updateRecentViewedItems();
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                hideLoading();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void updateRecentViewedItems() {
        database.updateRecentPosts(new Database.RecentUpdateCallback() {
            @Override
            public void onSuccess(List<HashMap<String, String>> reachedItems) {
                displayRecentViewedPostsPosts(reachedItems);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                hideLoading();
                Toast.makeText(getContext(), "No recently reached items.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayRecentViewedPostsPosts(final List<HashMap<String, String>> viewedPosts) {
        database.displayRecentPosts(viewedPosts, new Database.RecentDisplayCallback() {

            @Override
            public void onSuccess(HashMap<String, HashMap<String, String>> recentViewedPosts) {
                showRecentViewedPosts(recentViewedPosts);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                hideLoading();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showRecentViewedPosts(HashMap<String, HashMap<String, String>> recentViewedPosts) {

        List<HashMap.Entry<String, HashMap<String, String>>> list = new ArrayList<>(recentViewedPosts.entrySet());
        list.sort(new Comparator<HashMap.Entry<String, HashMap<String, String>>>() {
            @Override
            public int compare(HashMap.Entry<String, HashMap<String, String>> o1, HashMap.Entry<String, HashMap<String, String>> o2) {

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                try {
                    Date date1 = dateFormat.parse(o1.getValue().get("Date"));
                    Date date2 = dateFormat.parse(o2.getValue().get("Date"));
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
