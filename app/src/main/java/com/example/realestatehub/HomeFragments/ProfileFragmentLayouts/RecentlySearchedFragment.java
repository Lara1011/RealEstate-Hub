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

import com.example.realestatehub.HomeFragments.ReadPostAdapter.SearchAdapter;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class RecentlySearchedFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recently_searched, container, false);

        database = new Database(getContext());
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
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
        showLoading();
        database.updateRecentSearchItems(new Database.RecentDisplayCallback() {

            @Override
            public void onSuccess(HashMap<String, HashMap<String, String>> recentReachedPosts) {
                showRecentSearchPosts(recentReachedPosts);
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                hideLoading();
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRecentSearchPosts(HashMap<String, HashMap<String, String>> recentSearchPosts) {

        List<HashMap.Entry<String, HashMap<String, String>>> list = new ArrayList<>(recentSearchPosts.entrySet());
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

        HashMap<String, HashMap<String, String>> sortedRecentSearchPosts = new HashMap<>();
        for (HashMap.Entry<String, HashMap<String, String>> entry : list) {
            sortedRecentSearchPosts.put(entry.getKey(), entry.getValue());
        }

        hideLoading();
        SearchAdapter adapter = new SearchAdapter(sortedRecentSearchPosts);
        recyclerView.setAdapter(adapter);
    }
}
