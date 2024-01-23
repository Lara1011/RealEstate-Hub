package com.example.realestatehub;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class SetNewPasswordActivity extends AppCompatActivity {

    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //=========Device Bar Color=========
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.white));
        setContentView(R.layout.activity_set_new_password);

        initUI();
    }

    private void initUI(){
        continueBtn = findViewById(R.id.continue_btn);
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCongratulationDialog();
            }
        });
    }

    private void showCongratulationDialog(){
        View view = getLayoutInflater().inflate(R.layout.congratulation_dialog_layout,null);
        ImageView loadingImageView = view.findViewById(R.id.dialog_loading);
        Glide.with(this).asGif().load(R.drawable.loading2).into(loadingImageView);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();
    }
}