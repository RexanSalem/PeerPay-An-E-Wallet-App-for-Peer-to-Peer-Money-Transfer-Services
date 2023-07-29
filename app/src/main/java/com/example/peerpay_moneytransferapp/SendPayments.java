package com.example.peerpay_moneytransferapp;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

public class SendPayments extends AppCompatActivity {

    private TextView balanceTextView;
    private EditText nameEditText;
    private EditText numberEditText;
    private EditText amountEditText;
    private EditText dateEditText;
    private TextView textReferenceNumber;
    private Button sendMoneyButton;

    private DatabaseReference databaseReference;

    private EditText calendarIcon;

    private double walletBalance; // Initial balance

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_payments);

        nameEditText = findViewById(R.id.send_name);
        numberEditText = findViewById(R.id.send_number);
        amountEditText = findViewById(R.id.send_amount);
        dateEditText = findViewById(R.id.send_date);
        sendMoneyButton = findViewById(R.id.sendmoney_btn);
        balanceTextView = findViewById(R.id.balanceTextView);
        calendarIcon = findViewById(R.id.send_date);


        // Initialize the database reference
        databaseReference = FirebaseDatabase.getInstance().getReference().child("payments");

        walletBalance = 0.0;

        if (getIntent().hasExtra("wallet_balance")) {
            // If the extra is present, retrieve the wallet balance from the Intent
            walletBalance = getIntent().getDoubleExtra("wallet_balance", 0.0);
        } else {
            // If no extra, retrieve the wallet balance from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
            walletBalance = sharedPreferences.getFloat("walletBalance", 0.0f);
        }

        // Update the UI with the received wallet balance
        balanceTextView.setText(String.valueOf(walletBalance));


        sendMoneyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameEditText.getText().toString();
                String number = numberEditText.getText().toString();
                String amountString = amountEditText.getText().toString();

                // Validate amount as a double
                if (amountString.isEmpty()) {
                    showToast("Please enter an amount");
                    return;
                }

                double amount = Double.parseDouble(amountString);

                if (amount < 500) {
                    // Display an error message indicating the minimum limit has not been met
                    Toast.makeText(SendPayments.this, "Minimum send limit is 500", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (walletBalance < amount) {
                    // Display an error message indicating insufficient balance
                    Toast.makeText(SendPayments.this, "Insufficient Balance", Toast.LENGTH_SHORT).show();
                    return;
                }

                decreaseBalance(amount);

                // Get the current time
                String sendingTime = getCurrentTime();

                // Validate number as a long
                long numberValue;

                try {
                    numberValue = Long.parseLong(number);
                } catch (NumberFormatException e) {
                    showToast("Invalid number value");
                    return;
                }

                // Generate a reference number
                String referenceNumber = generateReferenceNumber();

                // Create a new Payment object
                Payment payment = new Payment(name, numberValue, amount, sendingTime, referenceNumber);

                // Save the payment in the Firebase database
                savePayment(payment);

                // Start SendMoneyNow activity with the generated reference number
                Intent intent = new Intent(SendPayments.this, SendMoneyNow.class);
                intent.putExtra(SendMoneyNow.NAME, name);
                intent.putExtra(SendMoneyNow.NUMBER, number);
                intent.putExtra(SendMoneyNow.AMOUNT, amount);
                intent.putExtra(SendMoneyNow.DATE, sendingTime);
                intent.putExtra(SendMoneyNow.REFERENCE_NUMBER, referenceNumber);
                startActivity(intent);
            }

            private void decreaseBalance(double amount) {
                if (walletBalance >= amount) {
                    walletBalance -= amount; // Subtract the amount from the wallet balance
                    updateBalance();

                    updateWalletBalanceInFirebase(walletBalance);
                }
            }
        });

        Button requestButton = findViewById(R.id.request_btn);
        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNewActivity();
            }
        });

        ImageView userToMainImageView = findViewById(R.id.send_tomain);
        userToMainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });

        calendarIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePicker();
            }
        });
    }

    private void updateBalance() {
        balanceTextView.setText(String.valueOf(walletBalance));
    }

    private void updateWalletBalanceInFirebase(double newBalance) {
        // Get the current user ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            // Update the user's wallet balance in Firebase database
            DatabaseReference userWalletRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId).child("walletBalance");
            userWalletRef.setValue(newBalance);
        }
    }

    private String getCurrentTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        return sdf.format(new Date());
    }

    private void savePayment(Payment payment) {
        String paymentId = databaseReference.push().getKey();
        databaseReference.child(paymentId).setValue(payment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showToast("Payment sent successfully!");
                    } else {
                        showToast("Failed to send payment.");
                    }
                });
    }

    private void showToast(String message) {
        Toast.makeText(SendPayments.this, message, Toast.LENGTH_SHORT).show();
    }

    private String generateReferenceNumber() {
        long timestamp = System.currentTimeMillis();
        int random = new Random().nextInt(1000); // Generates a random number between 0 and 999

        return "REF" + timestamp + random;
    }

    private void openSendActivity() {
        Intent intent = new Intent(this, SendPayments.class);
        startActivity(intent);
    }
    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void openNewActivity() {
        Intent intent = new Intent(this, RequestPayments.class);
        startActivity(intent);
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(SendPayments.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        String selectedDateText = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        dateEditText.setText(selectedDateText);
                    }
                }, year, month, day);
        datePickerDialog.show();
    }

    public static class Payment {
        private String name;
        private long number;
        private double amount;
        private String date;
        private String referenceNumber;

        public Payment() {
            // Default constructor required for Firebase
        }

        public Payment(String name, long number, double amount, String date, String referenceNumber) {
            this.name = name;
            this.number = number;
            this.amount = amount;
            this.date = date;
            this.referenceNumber = referenceNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public long getNumber() {
            return number;
        }

        public void setNumber(long number) {
            this.number = number;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getReferenceNumber() {
            return referenceNumber;
        }

        public void setReferenceNumber(String referenceNumber) {
            this.referenceNumber = referenceNumber;
        }
    }
}
