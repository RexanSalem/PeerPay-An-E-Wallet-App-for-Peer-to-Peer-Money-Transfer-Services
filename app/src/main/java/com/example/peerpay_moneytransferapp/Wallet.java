package com.example.peerpay_moneytransferapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import java.math.BigDecimal;

public class Wallet extends AppCompatActivity {

    private TextView balanceTextView;
    private double walletBalance;
    private ImageView linkacc;
    private PayPalConfiguration payPalConfig;

    public static final int REQUEST_CODE_PAYPAL_PAYMENT = 123;
    public static final String PAYPAL_CLIENT_ID = "AT0Yl1a966C94zsEMN0mhLfJxm6Eea_TMixawjFpOW_drRfQmygrZukJxd0BtguBRtZkrO3Ocx67KZaB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        balanceTextView = findViewById(R.id.balanceTextView);

        // Retrieve the wallet balance from the Intent
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


        // Create PayPalConfiguration object
        payPalConfig = new PayPalConfiguration()
                .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX) // Or ENVIRONMENT_PRODUCTION for live environment
                .clientId(PAYPAL_CLIENT_ID);

        // Start PayPal service
        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
        startService(intent);

        // Rest of your code
        linkacc = findViewById(R.id.link_paypal);
        linkacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

        ImageView userToMainImageView = findViewById(R.id.wallet_tomain);
        userToMainImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMainActivity();
            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(Wallet.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    private void processPayment() {
        PayPalPayment payment = new PayPalPayment(new BigDecimal("10.00"), "PHP", "Link Your Account",
                PayPalPayment.PAYMENT_INTENT_SALE);

        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, payPalConfig);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_CODE_PAYPAL_PAYMENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PAYPAL_PAYMENT) {
            if (resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirmation != null) {
                    String paymentDetails = confirmation.toJSONObject().toString();
                    // Process payment success
                    // You can parse payment details here
                }
            } else if (resultCode == RESULT_CANCELED) {
                // Payment canceled by user
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                // Invalid payment details
            }
        }
    }

}
