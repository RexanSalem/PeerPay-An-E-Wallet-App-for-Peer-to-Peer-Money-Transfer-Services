package com.example.peerpay_moneytransferapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class DisplayImage extends AppCompatActivity {

    private ImageView displayImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_image);

        displayImageView = findViewById(R.id.displayImageView);

        Intent intent = getIntent();
        if (intent != null) {
            String imageUriString = intent.getStringExtra("imageUri");
            if (imageUriString != null) {
                Uri imageUri = Uri.parse(imageUriString);
                Picasso.get().load(imageUri).into(displayImageView);
            }
        }
    }
}
