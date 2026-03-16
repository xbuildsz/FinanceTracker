package com.example.personalfinance.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.databinding.ActivityAddTransactionBinding;
import com.example.personalfinance.models.Transaction;
import com.example.personalfinance.utils.Constants;
import com.example.personalfinance.utils.FirebaseHelper;
import com.google.firebase.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddTransactionActivity extends AppCompatActivity {

    private ActivityAddTransactionBinding binding;
    private FirebaseHelper firebase;
    private Calendar selectedDate = Calendar.getInstance();
    private String transactionId = null;
    private Transaction existingTransaction = null;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddTransactionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebase = FirebaseHelper.getInstance();
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        transactionId = getIntent().getStringExtra("transactionId");
        if (transactionId != null) {
            getSupportActionBar().setTitle("Edit Transaction");
            loadTransactionData();
        } else {
            getSupportActionBar().setTitle("Add Transaction");
        }

        setupDatePicker();
        setupTypeSwitcher();
        // Hide receipt section — requires Firebase Storage (paid plan)
        binding.receiptContainer.setVisibility(View.GONE);
        setupSaveButton();

        binding.tvDate.setText(dateFormat.format(selectedDate.getTime()));
    }

    private void setupTypeSwitcher() {
        binding.rgType.setOnCheckedChangeListener((group, checkedId) -> {
            List<String> categories = checkedId == R.id.rbIncome
                    ? Constants.INCOME_CATEGORIES
                    : Constants.EXPENSE_CATEGORIES;
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, categories);
            binding.spinnerCategory.setAdapter(adapter);
        });
        binding.rbExpense.setChecked(true);
    }

    private void setupDatePicker() {
        binding.tvDate.setOnClickListener(v -> {
            new DatePickerDialog(this, (view, year, month, day) -> {
                selectedDate.set(year, month, day);
                binding.tvDate.setText(dateFormat.format(selectedDate.getTime()));
            }, selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void saveTransaction() {
        String title = binding.etTitle.getText().toString().trim();
        String amountStr = binding.etAmount.getText().toString().trim();
        String note = binding.etNote.getText().toString().trim();
        String category = binding.spinnerCategory.getSelectedItem() != null
                ? binding.spinnerCategory.getSelectedItem().toString() : "";
        String type = binding.rbIncome.isChecked() ? Constants.TYPE_INCOME : Constants.TYPE_EXPENSE;

        if (TextUtils.isEmpty(title)) { binding.etTitle.setError("Title required"); return; }
        if (TextUtils.isEmpty(amountStr)) { binding.etAmount.setError("Amount required"); return; }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            binding.etAmount.setError("Invalid amount");
            return;
        }

        setLoading(true);
        Timestamp date = new Timestamp(selectedDate.getTime());

        String userId = firebase.getCurrentUserId();
        Transaction transaction = new Transaction(userId, title, amount, type, category, note, date);

        if (transactionId != null) {
            transaction.setId(transactionId);
            firebase.updateTransaction(transaction, new FirebaseHelper.OnCompleteListener<Void>() {
                @Override public void onSuccess(Void result) {
                    setLoading(false);
                    Toast.makeText(AddTransactionActivity.this, "Transaction updated!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override public void onFailure(Exception e) {
                    setLoading(false);
                    Toast.makeText(AddTransactionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            firebase.addTransaction(transaction, new FirebaseHelper.OnCompleteListener<String>() {
                @Override public void onSuccess(String id) {
                    setLoading(false);
                    Toast.makeText(AddTransactionActivity.this, "Transaction added!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                @Override public void onFailure(Exception e) {
                    setLoading(false);
                    Toast.makeText(AddTransactionActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void loadTransactionData() {
        firebase.getDb().collection("transactions").document(transactionId)
                .get().addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        existingTransaction = doc.toObject(Transaction.class);
                        if (existingTransaction == null) return;
                        binding.etTitle.setText(existingTransaction.getTitle());
                        binding.etAmount.setText(String.valueOf(existingTransaction.getAmount()));
                        binding.etNote.setText(existingTransaction.getNote());
                        if (Constants.TYPE_INCOME.equals(existingTransaction.getType())) {
                            binding.rbIncome.setChecked(true);
                        } else {
                            binding.rbExpense.setChecked(true);
                        }
                        if (existingTransaction.getDate() != null) {
                            selectedDate.setTime(existingTransaction.getDate().toDate());
                            binding.tvDate.setText(dateFormat.format(selectedDate.getTime()));
                        }
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.btnSave.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
