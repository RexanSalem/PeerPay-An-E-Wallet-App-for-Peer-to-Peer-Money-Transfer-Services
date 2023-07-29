package com.example.peerpay_moneytransferapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RequestMoneyNow extends AppCompatActivity {
    public static final String NAME = "Name";
    public static final String NUMBER = "Number";
    public static final String AMOUNT = "Amount";
    public static final String DATE = "Date";
    public static final String REFERENCE_NUMBER = "ReferenceNumber";

    private TextView nameText, numberText, amountText, dateText, referenceNumberText;
    private String name;
    private String number;
    private double amount;
    private String date;
    private String referenceNumber;

    private Button requestAgainButton;
    private Button transactButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money_now);

        nameText = findViewById(R.id.rname);
        numberText = findViewById(R.id.rnumber);
        amountText = findViewById(R.id.ramount);
        dateText = findViewById(R.id.rdate);
        referenceNumberText = findViewById(R.id.text_reference_number);
        requestAgainButton = findViewById(R.id.requestagain_btn);
        transactButton = findViewById(R.id.transaction_btn);

        Intent i = getIntent();
        name = i.getStringExtra(NAME);
        number = i.getStringExtra(NUMBER); // Provide a default value
        amount = i.getDoubleExtra(AMOUNT, 0.0); // Provide a default value
        date = i.getStringExtra(DATE);
        referenceNumber = i.getStringExtra(REFERENCE_NUMBER);

        nameText.setText("Name: " + name);
        numberText.setText("Number: " + number);
        amountText.setText("Amount: " + amount);
        dateText.setText("Date: " + date);
        referenceNumberText.setText("Reference Number: " + referenceNumber);

        requestAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRequestPaymentsActivity();
            }
        });

        transactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransactionsActivity();
            }
        });
    }

    private void openRequestPaymentsActivity() {
        Intent intent = new Intent(this, RequestPayments.class);
        startActivity(intent);
    }

    private void openTransactionsActivity() {
        Intent intent = new Intent(this, RequestTransactions.class);
        startActivity(intent);
    }
}