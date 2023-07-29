package com.example.peerpay_moneytransferapp;

public class Transaction {
    private String name;
    private long number;
    private double amount;
    private String date;
    private String referenceNumber;

    public Transaction() {
    }

    public Transaction(String name, long number, double amount, String date, String referenceNumber) {
        this.name = name;
        this.number = number;
        this.amount = amount;
        this.date = date;
        this.referenceNumber = referenceNumber;
    }

    public String getName() {
        return name;
    }

    public long getNumber() {
        return number;
    }

    public double getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumber(long number) {
        this.number = number;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
