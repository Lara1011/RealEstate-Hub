/*
 * Copyright 2024 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.realestatehub.Utils.GooglePay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realestatehub.HomeFragments.HomeBottomNavigation;
import com.example.realestatehub.databinding.ActivityCheckoutSuccessBinding;

public class CheckoutSuccessActivity extends AppCompatActivity {
    private final int delay = 3500;
    private Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCheckoutSuccessBinding layoutBinding = ActivityCheckoutSuccessBinding.inflate(getLayoutInflater());
        setContentView(layoutBinding.getRoot());
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                    Intent intent = new Intent(CheckoutSuccessActivity.this, HomeBottomNavigation.class);
                    startActivity(intent);
                    finish();
            }
        }, delay);

    }
}