package com.example.realestatehub.HomeFragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.realestatehub.HomeFragments.UploadPost.AddPostActivity;
import com.example.realestatehub.HomeFragments.ProfileFragmentLayouts.LanguageUtils;
import com.example.realestatehub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeBottomNavigation extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // Set the content view directly
        replaceFragment(new HomeFragment());
        LanguageUtils.loadLocale(this);
        // Find views using findViewById
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.search) {
                replaceFragment(new SearchFragment());
            } else if (itemId == R.id.favorite) {
                replaceFragment(new FavoriteFragment());
            } else if (itemId == R.id.profile) {
                replaceFragment(new ProfileFragment());
            } else if (itemId == R.id.add) {
                showBottomDialog();
            }
            return true;
        });

    }
    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity2_add_post);

        LinearLayout rentLayout = dialog.findViewById(R.id.rent);
        LinearLayout sellLayout = dialog.findViewById(R.id.sell);
        LinearLayout postLayout = dialog.findViewById(R.id.layoutPost);

        rentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeBottomNavigation.this, AddPostActivity.class);
                dialog.dismiss();
                startActivity(intent);

                finish();
                //Toast.makeText(HomePageActivity.this, "Rent is clicked", Toast.LENGTH_SHORT).show();

            }
        });

        sellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeBottomNavigation.this, AddPostActivity.class);
                dialog.dismiss();
                startActivity(intent);

                finish();
                //Toast.makeText(HomePageActivity.this, "Sell is Clicked", Toast.LENGTH_SHORT).show();

            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }
}