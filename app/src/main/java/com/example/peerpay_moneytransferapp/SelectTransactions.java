package com.example.peerpay_moneytransferapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class SelectTransactions extends AppCompatActivity {

    private CardView cardsend, cardrequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_transactions);

        cardsend = findViewById(R.id.send_transac);
        cardrequest = findViewById(R.id.request_transac);

        cardsend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectTransactions.this, Transactions.class);
                startActivity(intent);
            }
        });

        cardrequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectTransactions.this, RequestTransactions.class);
                startActivity(intent);
            }
        });

        ImageView userToMainImageView = findViewById(R.id.selecttransactions_tomain);
        userToMainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(SelectTransactions.this, MainActivity.class);
        startActivity(intent);
    }
}
