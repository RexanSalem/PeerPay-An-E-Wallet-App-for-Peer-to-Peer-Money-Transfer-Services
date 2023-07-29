package com.example.peerpay_moneytransferapp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RequestTransactions extends AppCompatActivity {

    private ListView listView;
    private ArrayList<Transaction> transactionList;
    private TransactionAdapter adapter;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_transactions);

        // Initialize Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("request");

        // Initialize the ListView and transactionList
        listView = findViewById(R.id.listView);
        transactionList = new ArrayList<>();
        adapter = new TransactionAdapter(this, R.layout.activity_requestitemtransactions, transactionList);
        listView.setAdapter(adapter);

        // Retrieve transaction data from Firebase
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                // Retrieve the transaction data from the snapshot
                Transaction transaction = snapshot.getValue(Transaction.class);
                if (transaction != null) {
                    // Add the transaction to the list
                    transactionList.add(transaction);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            // Implement other ChildEventListener methods as needed

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                Toast.makeText(RequestTransactions.this, "Failed to retrieve transactions", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
