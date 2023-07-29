package com.example.peerpay_moneytransferapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class AboutApp extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        ImageView imageView = findViewById(R.id.back_tomain);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNextActivity();
            }

            private void openNextActivity() {
                Intent intent = new Intent(AboutApp.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}