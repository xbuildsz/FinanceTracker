package com.example.personalfinance.fragments;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.personalfinance.adapters.TransactionAdapter;
import com.example.personalfinance.databinding.FragmentTransactionsBinding;
import com.example.personalfinance.models.Transaction;
import com.example.personalfinance.utils.FirebaseHelper;
import java.util.ArrayList;
import java.util.List;

public class TransactionsFragment extends Fragment {

    private FragmentTransactionsBinding binding;
    private FirebaseHelper firebase;
    private TransactionAdapter adapter;
    private final List<Transaction> transactions = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentTransactionsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase = FirebaseHelper.getInstance();

        adapter = new TransactionAdapter(transactions, requireContext());
        binding.rvTransactions.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvTransactions.setAdapter(adapter);

        setupFilters();
        loadTransactions("all");
    }

    private void setupFilters() {
        binding.chipAll.setOnClickListener(v -> loadTransactions("all"));
        binding.chipIncome.setOnClickListener(v -> loadTransactions("income"));
        binding.chipExpense.setOnClickListener(v -> loadTransactions("expense"));
    }

    private void loadTransactions(String filter) {
        String userId = firebase.getCurrentUserId();
        if (userId == null) return;

        com.google.firebase.firestore.Query query;
        if ("all".equals(filter)) {
            query = firebase.getTransactionsQuery(userId);
        } else {
            query = firebase.getTransactionsByType(userId, filter);
        }

        query.addSnapshotListener((snap, e) -> {
            if (e != null || snap == null || !isAdded()) return;
            transactions.clear();
            for (com.google.firebase.firestore.DocumentSnapshot doc : snap.getDocuments()) {
                Transaction t = doc.toObject(Transaction.class);
                if (t != null) transactions.add(t);
            }
            adapter.notifyDataSetChanged();
            binding.tvEmpty.setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
