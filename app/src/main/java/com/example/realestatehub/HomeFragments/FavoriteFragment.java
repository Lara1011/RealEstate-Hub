package com.example.realestatehub.HomeFragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;

import java.util.HashMap;

public class FavoriteFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private Database database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorite, container, false);

        initUI();
        updateFavoriteData();

        return view;
    }

    private void initUI() {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        database = new Database(getContext());
    }

    private void updateFavoriteData() {
        database.readUsersData(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                database.updateFavoriteData(new Database.PostsCallback() {
                    @Override
                    public void onSuccess(HashMap<String, HashMap<String, String>> postList) {
                        PostAdapter adapter = new PostAdapter(postList);
                        recyclerView.setAdapter(adapter);
                    }
                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(int errorCode, String errorMessage) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

}