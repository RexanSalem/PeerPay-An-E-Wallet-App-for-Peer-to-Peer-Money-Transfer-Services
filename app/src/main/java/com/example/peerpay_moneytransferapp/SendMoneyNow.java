package com.example.peerpay_moneytransferapp;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SendMoneyNow extends AppCompatActivity {

    public static final String NAME = "Name";
    public static final String NUMBER = "Number";
    public static final String AMOUNT = "Amount";
    public static final String DATE = "Date";
    public static final String REFERENCE_NUMBER = "ReferenceNumber";
    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String FILENAME = "Transaction.pdf";

    private TextView nameText, numberText, amountText, dateText, referenceNumberText;
    private String name;
    private String number;
    private double amount;
    private String date;
    private String referenceNumber;

    private Button sendAgainButton;
    private Button transactButton;

    private Button downloadButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_money_now);

        nameText = findViewById(R.id.sname);
        numberText = findViewById(R.id.snumber);
        amountText = findViewById(R.id.samount);
        dateText = findViewById(R.id.sdate);
        referenceNumberText = findViewById(R.id.text_reference_number);
        sendAgainButton = findViewById(R.id.sendagain_btn);
        transactButton = findViewById(R.id.transaction_btn);
        downloadButton = findViewById(R.id.download_btn);

        Intent intent = getIntent();
        name = intent.getStringExtra(NAME);
        number = intent.getStringExtra(NUMBER);
        amount = intent.getDoubleExtra(AMOUNT, 0.0);
        date = intent.getStringExtra(DATE);
        referenceNumber = intent.getStringExtra(REFERENCE_NUMBER);

        nameText.setText("Name: " + name);
        numberText.setText("Number: " + number);
        amountText.setText("Amount: " + amount);
        dateText.setText("Date: " + date);
        referenceNumberText.setText("Reference Number: " + referenceNumber);

        sendAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSendPaymentsActivity();
            }
        });

        transactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTransactionsActivity();
            }
        });

        downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    saveDataToGallery();
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestStoragePermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
    }

    private void saveDataToGallery() {
        try {
            // Create a bitmap with the data to be saved
            Bitmap bitmap = createBitmapWithData();

            // Save the bitmap to the gallery
            String fileName = "transaction_" + System.currentTimeMillis() + ".png";
            String path = Environment.getExternalStorageDirectory().toString();
            File file = new File(path, fileName);

            OutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

            // Add the image to the gallery
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, fileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Transaction Receipt");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

            getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            Toast.makeText(this, "Transaction saved to Gallery", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap createBitmapWithData() {
        // Create a bitmap and draw the data on it
        Bitmap bitmap = Bitmap.createBitmap(800, 600, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);

        // Set the text properties
        Paint paint = new Paint();
        paint.setTextSize(30);
        paint.setColor(Color.BLACK);

        // Draw the data on the bitmap
        int x = 20;
        int y = 40;

        canvas.drawText("Name: " + name, x, y, paint);
        y += 40;
        canvas.drawText("Number: " + number, x, y, paint);
        y += 40;
        canvas.drawText("Amount: " + amount, x, y, paint);
        y += 40;
        canvas.drawText("Date: " + date, x, y, paint);
        y += 40;
        canvas.drawText("Reference Number: " + referenceNumber, x, y, paint);

        return bitmap;
    }

    private void openSendPaymentsActivity() {
        Intent intent = new Intent(this, SendPayments.class);
        startActivity(intent);
    }

    private void openTransactionsActivity() {
        Intent intent = new Intent(this, Transactions.class);
        startActivity(intent);
    }
}
