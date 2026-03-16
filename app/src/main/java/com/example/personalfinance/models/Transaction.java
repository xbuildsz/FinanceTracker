package com.example.personalfinance.models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

public class Transaction {

    @DocumentId
    private String id;
    private String userId;
    private String title;
    private double amount;
    private String type; // "income" or "expense"
    private String category;
    private String note;
    private String receiptUrl;
    private Timestamp date;
    private Timestamp createdAt;

    public Transaction() {}

    public Transaction(String userId, String title, double amount, String type,
                       String category, String note, Timestamp date) {
        this.userId = userId;
        this.title = title;
        this.amount = amount;
        this.type = type;
        this.category = category;
        this.note = note;
        this.date = date;
        this.createdAt = Timestamp.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getReceiptUrl() { return receiptUrl; }
    public void setReceiptUrl(String receiptUrl) { this.receiptUrl = receiptUrl; }
    public Timestamp getDate() { return date; }
    public void setDate(Timestamp date) { this.date = date; }
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
