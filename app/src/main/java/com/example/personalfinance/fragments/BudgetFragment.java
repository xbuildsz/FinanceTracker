package com.example.personalfinance.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.personalfinance.R;
import com.example.personalfinance.adapters.BudgetAdapter;
import com.example.personalfinance.databinding.FragmentBudgetBinding;
import com.example.personalfinance.models.Budget;
import com.example.personalfinance.models.Transaction;
import com.example.personalfinance.utils.Constants;
import com.example.personalfinance.utils.FirebaseHelper;
import java.util.*;

public class BudgetFragment extends Fragment {

    private FragmentBudgetBinding binding;
    private FirebaseHelper firebase;
    private BudgetAdapter adapter;
    private final List<Budget> budgets = new ArrayList<>();
    private final Calendar calendar = Calendar.getInstance();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase = FirebaseHelper.getInstance();

        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        binding.tvMonth.setText(Constants.MONTHS[month - 1] + " " + year);

        adapter = new BudgetAdapter(budgets, requireContext());
        binding.rvBudgets.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvBudgets.setAdapter(adapter);

        binding.fabAddBudget.setOnClickListener(v -> showAddBudgetDialog());

        loadBudgets(month, year);
        syncSpentAmounts(month, year);
    }

    private void loadBudgets(int month, int year) {
        String userId = firebase.getCurrentUserId();
        if (userId == null) return;

        firebase.getBudgetsQuery(userId, month, year)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null || !isAdded()) return;
                    budgets.clear();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snap.getDocuments()) {
                        Budget b = doc.toObject(Budget.class);
                        if (b != null) budgets.add(b);
                    }
                    adapter.notifyDataSetChanged();
                    binding.tvEmpty.setVisibility(budgets.isEmpty() ? View.VISIBLE : View.GONE);
                });
    }

    private void syncSpentAmounts(int month, int year) {
        String userId = firebase.getCurrentUserId();
        if (userId == null) return;

        Calendar startCal = Calendar.getInstance();
        startCal.set(year, month - 1, 1, 0, 0, 0);
        Calendar endCal = Calendar.getInstance();
        endCal.set(year, month - 1, startCal.getActualMaximum(Calendar.DAY_OF_MONTH), 23, 59, 59);

        firebase.getDb().collection("transactions")
                .whereEqualTo("userId", userId)
                .whereEqualTo("type", Constants.TYPE_EXPENSE)
                .whereGreaterThanOrEqualTo("date", new com.google.firebase.Timestamp(startCal.getTime()))
                .whereLessThanOrEqualTo("date", new com.google.firebase.Timestamp(endCal.getTime()))
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null || !isAdded()) return;

                    Map<String, Double> categorySpend = new HashMap<>();
                    for (com.google.firebase.firestore.DocumentSnapshot doc : snap.getDocuments()) {
                        Transaction t = doc.toObject(Transaction.class);
                        if (t != null) {
                            categorySpend.merge(t.getCategory(), t.getAmount(), Double::sum);
                        }
                    }

                    // Update each budget's spent amount
                    for (Budget b : budgets) {
                        double spent = categorySpend.getOrDefault(b.getCategory(), 0.0);
                        if (b.getSpent() != spent) {
                            b.setSpent(spent);
                            firebase.updateBudget(b, new FirebaseHelper.OnCompleteListener<Void>() {
                                @Override public void onSuccess(Void r) {}
                                @Override public void onFailure(Exception ex) {}
                            });
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void showAddBudgetDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_budget, null);
        Spinner spinnerCategory = dialogView.findViewById(R.id.spinnerCategory);
        EditText etLimit = dialogView.findViewById(R.id.etLimit);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, Constants.EXPENSE_CATEGORIES);
        spinnerCategory.setAdapter(adapter);

        new AlertDialog.Builder(requireContext())
                .setTitle("Set Budget")
                .setView(dialogView)
                .setPositiveButton("Save", (dialog, which) -> {
                    String category = spinnerCategory.getSelectedItem().toString();
                    String limitStr = etLimit.getText().toString().trim();
                    if (!limitStr.isEmpty()) {
                        double limit = Double.parseDouble(limitStr);
                        int month = calendar.get(Calendar.MONTH) + 1;
                        int year = calendar.get(Calendar.YEAR);
                        Budget budget = new Budget(firebase.getCurrentUserId(), category, limit, month, year);
                        firebase.addBudget(budget, new FirebaseHelper.OnCompleteListener<String>() {
                            @Override public void onSuccess(String id) {}
                            @Override public void onFailure(Exception e) {
                                Toast.makeText(requireContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
