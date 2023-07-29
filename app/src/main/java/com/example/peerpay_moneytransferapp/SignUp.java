package com.example.peerpay_moneytransferapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Random;

public class SignUp extends AppCompatActivity {

    private EditText signupFullname, signupEmail, signupPassword, signupNumber;
    private Button signupButton;
    private TextView loginRedirectText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        signupFullname = findViewById(R.id.signup_fullname);
        signupEmail = findViewById(R.id.signup_email);
        signupPassword = findViewById(R.id.signup_password);
        signupNumber = findViewById(R.id.signup_number);
        signupButton = findViewById(R.id.button_signup);
        loginRedirectText = findViewById(R.id.login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = signupFullname.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String pass = signupPassword.getText().toString().trim();
                String number = signupNumber.getText().toString().trim();

                if (user.isEmpty()) {
                    signupFullname.setError("Fullname cannot be empty");
                } else if (email.isEmpty()) {
                    signupEmail.setError("Email cannot be empty");
                } else if (pass.isEmpty()) {
                    signupPassword.setError("Password cannot be empty");
                } else if (number.isEmpty()) {
                    signupNumber.setError("Phone Number cannot be empty");
                } else {
                    mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendVerificationEmail(mAuth); // Send verification email
                            } else {
                                Toast.makeText(SignUp.this, "SignUp Failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUp.this, Verification.class));
            }
        });
    }

    private void sendVerificationEmail(FirebaseAuth mAuth) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // Generate a 6-digit verification code
            Random random = new Random();
            int verificationCode = 100000 + random.nextInt(900000);

            // Convert the verification code to a string
            String verificationCodeString = String.valueOf(verificationCode);

            user.sendEmailVerification().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignUp.this, "Verification email sent. Please check your inbox.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUp.this, Verification.class));
                        finish();
                    } else {
                        Toast.makeText(SignUp.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
