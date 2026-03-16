package com.example.personalfinance.models;

import com.google.firebase.firestore.DocumentId;

public class Budget {

    @DocumentId
    private String id;
    private String userId;
    private String category;
    private double limit;
    private double spent;
    private int month;
    private int year;

    public Budget() {}

    public Budget(String userId, String category, double limit, int month, int year) {
        this.userId = userId;
        this.category = category;
        this.limit = limit;
        this.spent = 0;
        this.month = month;
        this.year = year;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }
    public double getSpent() { return spent; }
    public void setSpent(double spent) { this.spent = spent; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public double getPercentage() {
        if (limit == 0) return 0;
        return (spent / limit) * 100;
    }

    public double getRemaining() {
        return limit - spent;
    }
}
