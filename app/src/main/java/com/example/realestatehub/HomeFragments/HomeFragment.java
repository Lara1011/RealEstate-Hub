package com.example.realestatehub.HomeFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realestatehub.Utils.Database;
import com.example.realestatehub.HomeFragments.ReadPostAdapter.PostAdapter;
import com.example.realestatehub.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends Fragment {

    // Declaration of variables to hold view references and Firebase data
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CircleImageView userImg;
    private TextView userName;
    BottomNavigationView bottomNavigationView;
    private Database database;

    public HomeFragment(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        userImg = view.findViewById(R.id.user_img);
        userName = view.findViewById(R.id.user_name);
        view.findViewById(R.id.user_lay).setOnClickListener(v -> bottomNavigationView.setSelectedItemId(R.id.profile));

        // Initialize the ProgressBar and RecyclerView
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext())); // Set the layout manager

        database = new Database(getContext());
        // Method call to start loading user data from Firebase
        readUsersAndPosts();
        updateProfileUI();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        database.getFirebaseUser().reload().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    updateProfileUI();
                } else {
                    Toast.makeText(getContext(), "Failed to refresh user profile", Toast.LENGTH_SHORT).show();
                }
            }
        });
        updateProfileUI();
    }

    private void updateProfileUI() {
        database.CheckCurrUserIfExists(new Database.GeneralCallback() {
            @Override
            public void onSuccess() {
                userName.setText(String.format(database.getReadWriteUserDetails().getFirstName() + " " + database.getReadWriteUserDetails().getLastName()));
                Picasso.get().load(database.getFirebaseUser().getPhotoUrl()).into(userImg);
            }


            @Override
            public void onFailure(int errorCode, String errorMessage) {
                if (errorCode == 0) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Method to show the progress bar when loading data
    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
    }

    // Method to hide the progress bar once data is loaded
    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
    }

    // Method to read user data from Firebase
    private void readUsersAndPosts() {
        showLoading();
        database.readUsersData(new Database.GeneralCallback() {

            @Override
            public void onSuccess() {
                database.readPostsData(new Database.PostsCallback() {
                    @Override
                    public void onSuccess(HashMap<String, HashMap<String, String>> postList) {
                        hideLoading();
                        // Create and set an adapter for the RecyclerView with the loaded post data
                        PostAdapter adapter = new PostAdapter(postList);
                        recyclerView.setAdapter(adapter);
                    }


                    @Override
                    public void onFailure(int errorCode, String errorMessage) {
                        hideLoading(); // Hide the progress bar on error
                        if (errorCode == 0) {
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();}
                    }
                });
            }

            @Override
            public void onFailure(int errorCode, String errorMessage) {
                hideLoading(); // Hide the progress bar on error
                if (errorCode == 0) {
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
