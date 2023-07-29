package com.example.peerpay_moneytransferapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private CardView card1, card2, card3;
    private TextView mainViewAll;
    private BottomNavigationView bottomNavigationView;

    private TextView balanceTextView;
    private double walletBalance = 0.0;
    private static final int REQUEST_CASH_IN = 1;

    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseApp.initializeApp(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        balanceTextView = findViewById(R.id.balanceTextView);

        ImageView imageView = findViewById(R.id.logo_about);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNextActivity();
            }

            private void openNextActivity() {
                Intent intent = new Intent(MainActivity.this, AboutApp.class);
                startActivity(intent);
            }
        });

        card1 = findViewById(R.id.payment);
        card2 = findViewById(R.id.wallet);
        card3 = findViewById(R.id.transaction);
        mainViewAll = findViewById(R.id.main_viewall);
        bottomNavigationView = findViewById(R.id.buttom_navMenu);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        mainViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectTransactions.class);
                startActivity(intent);
            }
        });

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home) {
                    Toast.makeText(MainActivity.this, "Home Clicked", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.profile) {
                    Toast.makeText(MainActivity.this, "Profile Clicked", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserProfile.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }
        });

        Button cashInButton = findViewById(R.id.cashInButton);
        cashInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cashInIntent = new Intent(MainActivity.this, CashInActivity.class);
                startActivityForResult(cashInIntent, REQUEST_CASH_IN);
            }
        });

        // Get the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            // Add ValueEventListener to retrieve wallet balance
            DatabaseReference userWalletRef = databaseReference.child("users").child(userId).child("walletBalance");
            userWalletRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Retrieve the wallet balance from Firebase
                        walletBalance = snapshot.getValue(Double.class);
                        // Update the UI with the received wallet balance
                        balanceTextView.setText(String.valueOf(walletBalance));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors that occur while fetching data
                    Toast.makeText(MainActivity.this, "Error fetching wallet balance: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Handle the case when the user is not authenticated or signed in
            // You can display an error message or redirect the user to the login screen.
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.payment) {
            Intent intent = new Intent(MainActivity.this, SendPayments.class);
            intent.putExtra("wallet_balance", walletBalance);
            startActivity(intent);
        } else if (v.getId() == R.id.wallet) {
            Intent intent = new Intent(MainActivity.this, Wallet.class);
            intent.putExtra("wallet_balance", walletBalance);
            startActivity(intent);
        } else if (v.getId() == R.id.transaction) {
            Intent intent = new Intent(MainActivity.this, SelectTransactions.class);
            startActivity(intent);
        } else {
            Toast.makeText(MainActivity.this, "Invalid button click", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CASH_IN && resultCode == RESULT_OK) {
            if (data != null) {
                double cashInAmount = data.getDoubleExtra(CashInActivity.EXTRA_CASH_IN_AMOUNT, 0.0);
                walletBalance += cashInAmount;
                balanceTextView.setText(String.valueOf(walletBalance));

                // Update the wallet balance in Firebase database
                updateWalletBalanceInFirebase(walletBalance);
            }
        }
    }

    private void updateWalletBalanceInFirebase(double newBalance) {
        // Get the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
        } else {
            // Handle the case when the user is not authenticated or signed in
            // You can display an error message or redirect the user to the login screen.
            return;
        }

        // Update the user's wallet balance in Firebase database
        DatabaseReference userWalletRef = databaseReference.child("users").child(userId).child("walletBalance");
        userWalletRef.setValue(newBalance)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Wallet balance updated successfully
                            walletBalance = newBalance;
                            balanceTextView.setText(String.valueOf(walletBalance));

                            // Save the updated wallet balance in SharedPreferences
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putFloat("walletBalance", (float) walletBalance);
                            editor.apply();
                        } else {
                            // Failed to update wallet balance
                            Toast.makeText(MainActivity.this, "Failed to update wallet balance", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
