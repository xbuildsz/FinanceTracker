package com.example.personalfinance.utils;

import com.example.personalfinance.models.Budget;
import com.example.personalfinance.models.Transaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class FirebaseHelper {

    private static FirebaseHelper instance;
    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private static final String COLLECTION_TRANSACTIONS = "transactions";
    private static final String COLLECTION_BUDGETS = "budgets";

    private FirebaseHelper() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static FirebaseHelper getInstance() {
        if (instance == null) {
            instance = new FirebaseHelper();
        }
        return instance;
    }

    public FirebaseAuth getAuth() { return auth; }
    public FirebaseFirestore getDb() { return db; }
    public FirebaseUser getCurrentUser() { return auth.getCurrentUser(); }
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }

    // ─── TRANSACTIONS ──────────────────────────────────────────────────────────

    public void addTransaction(Transaction transaction, OnCompleteListener<String> listener) {
        db.collection(COLLECTION_TRANSACTIONS)
                .add(transaction)
                .addOnSuccessListener(ref -> listener.onSuccess(ref.getId()))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateTransaction(Transaction transaction, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_TRANSACTIONS)
                .document(transaction.getId())
                .set(transaction)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteTransaction(String transactionId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_TRANSACTIONS)
                .document(transactionId)
                .delete()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    public Query getTransactionsQuery(String userId) {
        return db.collection(COLLECTION_TRANSACTIONS)
                .whereEqualTo("userId", userId)
                .orderBy("date", Query.Direction.DESCENDING);
    }

    public Query getTransactionsByType(String userId, String type) {
        return db.collection(COLLECTION_TRANSACTIONS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", type)
                .orderBy("date", Query.Direction.DESCENDING);
    }

    // ─── BUDGETS ───────────────────────────────────────────────────────────────

    public void addBudget(Budget budget, OnCompleteListener<String> listener) {
        db.collection(COLLECTION_BUDGETS)
                .add(budget)
                .addOnSuccessListener(ref -> listener.onSuccess(ref.getId()))
                .addOnFailureListener(listener::onFailure);
    }

    public void updateBudget(Budget budget, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_BUDGETS)
                .document(budget.getId())
                .set(budget)
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    public void deleteBudget(String budgetId, OnCompleteListener<Void> listener) {
        db.collection(COLLECTION_BUDGETS)
                .document(budgetId)
                .delete()
                .addOnSuccessListener(listener::onSuccess)
                .addOnFailureListener(listener::onFailure);
    }

    public Query getBudgetsQuery(String userId, int month, int year) {
        return db.collection(COLLECTION_BUDGETS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("month", month)
                .whereEqualTo("year", year);
    }

    // ─── LISTENER INTERFACE ────────────────────────────────────────────────────

    public interface OnCompleteListener<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}
