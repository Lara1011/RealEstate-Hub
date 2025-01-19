package com.example.realestatehub.HomeFragments;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.R;
import com.example.realestatehub.Utils.Database;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                navigateToHomeFragment();
            }
        });

        return view;
    }
    private void navigateToHomeFragment() {
        Log.e("FavoriteFragment", "Back Button Pressed");
        BottomNavigationView bottomNavigationView = requireActivity().findViewById(R.id.bottomNavigationView);
        if (bottomNavigationView != null) {
            bottomNavigationView.setSelectedItemId(R.id.home);
        } else {
            Toast.makeText(getContext(), "Navigation error", Toast.LENGTH_SHORT).show();
        }
    }

    private void initUI() {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        database = Database.getInstance(getContext());
    }

    private void updateFavoriteData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference favoriteRef = FirebaseDatabase.getInstance().getReference("Users Favorites").child(uid);

        favoriteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HashMap<String, HashMap<String, String>> userFavorites = new HashMap<>();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String postId = postSnapshot.getKey();
                    HashMap<String, String> postDetails = (HashMap<String, String>) postSnapshot.getValue();
                    userFavorites.put(postId, postDetails);
                }
                PostAdapter adapter = new PostAdapter(userFavorites);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }


}