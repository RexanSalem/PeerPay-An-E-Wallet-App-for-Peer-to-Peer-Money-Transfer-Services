package com.example.peerpay_moneytransferapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class CashInActivity extends AppCompatActivity {

    public static final String EXTRA_CASH_IN_AMOUNT = "cash_in_amount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cash_in);

        Button proceedButton = findViewById(R.id.proceedButton);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText cashInAmountEditText = findViewById(R.id.cashInAmountEditText);
                String cashInAmountString = cashInAmountEditText.getText().toString();

                if (!cashInAmountString.isEmpty()) {
                    double cashInAmount = Double.parseDouble(cashInAmountString);

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_CASH_IN_AMOUNT, cashInAmount);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }
            }
        });
    }
}
