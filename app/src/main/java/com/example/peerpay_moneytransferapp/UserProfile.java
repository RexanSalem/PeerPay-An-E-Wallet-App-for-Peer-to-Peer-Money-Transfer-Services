package com.example.peerpay_moneytransferapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;

public class UserProfile extends AppCompatActivity {

    private TextView Gmail_Profile, Logout_Profile;
    private FirebaseUser user;
    FirebaseAuth auth;

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imageView;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imageView = findViewById(R.id.displayImageView);
        Button btnChoose = findViewById(R.id.btnChoose);
        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        Gmail_Profile = findViewById(R.id.gmail_profile);
        Logout_Profile = findViewById(R.id.logout_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        } else {
            Gmail_Profile.setText(user.getEmail());
        }

        Logout_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                startActivity(intent);
                finish();
            }
        });

        ImageView userToMainImageView = findViewById(R.id.user_tomain);
        userToMainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                imageView.setImageBitmap(bitmap);
                openUserProfile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void openUserProfile() {
        Intent intent = new Intent(this, DisplayImage.class);
        intent.putExtra("imageUri", selectedImageUri.toString());
        startActivity(intent);
    }

    private void openMainActivity() {
        Intent intent = new Intent(UserProfile.this, MainActivity.class);
        startActivity(intent);
    }
}
