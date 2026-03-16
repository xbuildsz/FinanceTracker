package com.example.personalfinance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.personalfinance.R;
import com.example.personalfinance.databinding.ActivityTransactionDetailBinding;
import com.example.personalfinance.models.Transaction;
import com.example.personalfinance.utils.Constants;
import com.example.personalfinance.utils.FirebaseHelper;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class TransactionDetailActivity extends AppCompatActivity {

    private ActivityTransactionDetailBinding binding;
    private FirebaseHelper firebase;
    private String transactionId;
    private Transaction transaction;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMM dd yyyy", Locale.getDefault());
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebase = FirebaseHelper.getInstance();
        transactionId = getIntent().getStringExtra("transactionId");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Transaction Details");

        loadTransaction();

        binding.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra("transactionId", transactionId);
            startActivity(intent);
        });

        binding.btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadTransaction() {
        firebase.getDb().collection("transactions").document(transactionId)
                .addSnapshotListener((doc, e) -> {
                    if (e != null || doc == null || !doc.exists()) return;
                    transaction = doc.toObject(Transaction.class);
                    if (transaction != null) displayTransaction();
                });
    }

    private void displayTransaction() {
        binding.tvTitle.setText(transaction.getTitle());
        binding.tvCategory.setText(transaction.getCategory());
        binding.tvNote.setText(transaction.getNote() != null && !transaction.getNote().isEmpty()
                ? transaction.getNote() : "No note");

        boolean isIncome = Constants.TYPE_INCOME.equals(transaction.getType());
        binding.tvAmount.setText((isIncome ? "+ " : "- ") +
                currencyFormat.format(transaction.getAmount()));
        binding.tvAmount.setTextColor(getColor(isIncome ? R.color.colorIncome : R.color.colorExpense));
        binding.tvType.setText(isIncome ? "Income" : "Expense");
        binding.tvType.setBackgroundTintList(getColorStateList(
                isIncome ? R.color.colorIncomeLight : R.color.colorExpenseLight));

        if (transaction.getDate() != null) {
            binding.tvDate.setText(dateFormat.format(transaction.getDate().toDate()));
        }

        if (transaction.getReceiptUrl() != null && !transaction.getReceiptUrl().isEmpty()) {
            binding.receiptSection.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(transaction.getReceiptUrl())
                    .placeholder(R.drawable.ic_image_placeholder)
                    .into(binding.ivReceipt);
        } else {
            binding.receiptSection.setVisibility(View.GONE);
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTransaction())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteTransaction() {
        firebase.deleteTransaction(transactionId, new FirebaseHelper.OnCompleteListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(TransactionDetailActivity.this, "Deleted!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TransactionDetailActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
