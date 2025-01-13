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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.example.realestatehub.Utils.Database;
import com.example.realestatehub.HomeFragments.UploadPost.AddPostActivity;
import com.example.realestatehub.Utils.Language;
import com.example.realestatehub.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class HomeBottomNavigation extends AppCompatActivity {
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page); // Set the content view directly
        database = Database.getInstance(this);

        initUI();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        replaceFragment(new HomeFragment(bottomNavigationView));

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                replaceFragment(new HomeFragment(bottomNavigationView));
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

    @Override
    public void onBackPressed() {}

    private void initUI() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        Language.loadLocale(this);

        // Clear the current menu to avoid duplicate items
        bottomNavigationView.getMenu().clear();

        // Inflate the default menu
        bottomNavigationView.inflateMenu(R.menu.bottom_menu);

        // Find views using findViewById
        Log.d("HomeBottomNavigation", "initUI: " + database.getReadWriteUserDetails().getPurpose() + " " + database.getReadWriteUserDetails().getFirstName());
        if (database.getReadWriteUserDetails().getPurpose().equals("Buyer")) {
            bottomNavigationView.getMenu().removeItem(R.id.add);
        } else if (database.getReadWriteUserDetails().getPurpose().equals("Seller")) {
            bottomNavigationView.getMenu().removeItem(R.id.favorite);
            bottomNavigationView.getMenu().removeItem(R.id.search);
        }
        bottomNavigationView.setBackground(null);

    }

    private void showBottomDialog() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity2_add_post);

        LinearLayout rentLayout = dialog.findViewById(R.id.rent);
        LinearLayout sellLayout = dialog.findViewById(R.id.sell);

        rentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeBottomNavigation.this, AddPostActivity.class);
                intent.putExtra("Post Type", "rent");
                intent.putExtra("Post Owner Id", database.getReadWriteUserDetails().getId());
                dialog.dismiss();
                startActivity(intent);

                finish();
            }
        });

        sellLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeBottomNavigation.this, AddPostActivity.class);
                intent.putExtra("Post Type", "sell");
                intent.putExtra("Post Owner Id", database.getReadWriteUserDetails().getId());
                dialog.dismiss();
                startActivity(intent);

                finish();
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