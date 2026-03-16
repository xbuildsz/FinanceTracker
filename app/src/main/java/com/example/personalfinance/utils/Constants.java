package com.example.personalfinance.utils;

import java.util.Arrays;
import java.util.List;

public class Constants {

    public static final String TYPE_INCOME = "income";
    public static final String TYPE_EXPENSE = "expense";

    public static final List<String> EXPENSE_CATEGORIES = Arrays.asList(
            "Food & Dining",
            "Shopping",
            "Transportation",
            "Housing",
            "Healthcare",
            "Entertainment",
            "Education",
            "Travel",
            "Bills & Utilities",
            "Personal Care",
            "Other"
    );

    public static final List<String> INCOME_CATEGORIES = Arrays.asList(
            "Salary",
            "Freelance",
            "Business",
            "Investment",
            "Gift",
            "Other"
    );

    public static final int[] CATEGORY_COLORS = {
            0xFFE53935, 0xFFD81B60, 0xFF8E24AA, 0xFF5E35B1,
            0xFF1E88E5, 0xFF00ACC1, 0xFF43A047, 0xFFFB8C00,
            0xFFFF7043, 0xFF6D4C41, 0xFF546E7A
    };

    public static final String[] MONTHS = {
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
    };
}
