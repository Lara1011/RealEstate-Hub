
package com.example.realestatehub.FetchUserData;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.R;

public class MainActivity extends AppCompatActivity {
    private static final String PREF_NAME = "MyAppPreferences";
    private static final String PREF_KEY_FIRST_LAUNCH = "firstLaunch";
    private Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity1_welcome);
        // Check if it's the first launch
        if (isFirstLaunch()) {
            // Show the Welcome layout
            showWelcomeLayout();
            // Update SharedPreferences to indicate that the user has seen the Welcome layout
            setFirstLaunchFlag(false);
        } else {
            // Show the LogIn layout
            showLoginLayout();
        }
    }
    private boolean isFirstLaunch() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(PREF_KEY_FIRST_LAUNCH, true);
    }

    private void setFirstLaunchFlag(boolean isFirstLaunch) {
        SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(PREF_KEY_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }

    private void showWelcomeLayout() {
        startActivity(new Intent(this, WelcomeActivity.class));
    }

    private void showLoginLayout() {
        startActivity(new Intent(this, LoadingActivity.class));
    }

}
