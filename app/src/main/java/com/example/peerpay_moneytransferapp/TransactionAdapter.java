package com.example.peerpay_moneytransferapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class TransactionAdapter extends ArrayAdapter<Transaction> {

    private Context context;
    private int resource;
    private ArrayList<Transaction> transactionList;

    public TransactionAdapter(Context context, int resource, ArrayList<Transaction> transactionList) {
        super(context, resource, transactionList);
        this.context = context;
        this.resource = resource;
        this.transactionList = transactionList;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(resource, parent, false);
        }

        TextView textName = convertView.findViewById(R.id.textName);
        TextView textNumber = convertView.findViewById(R.id.textNumber);
        TextView textAmount = convertView.findViewById(R.id.textAmount);
        TextView textDate = convertView.findViewById(R.id.textDate);
        TextView textReferenceNumber = convertView.findViewById(R.id.textReferenceNumber);

        Transaction transaction = transactionList.get(position);

        textName.setText("Name: " + transaction.getName());
        textNumber.setText("Number: " + transaction.getNumber());
        textAmount.setText("Amount: " + transaction.getAmount());
        textDate.setText("Date: " + transaction.getDate());
        textReferenceNumber.setText("Reference Number: " + transaction.getReferenceNumber());

        return convertView;
    }
}
