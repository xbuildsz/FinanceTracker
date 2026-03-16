package com.example.personalfinance.fragments;

import android.os.Bundle;
import android.view.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.personalfinance.R;
import com.example.personalfinance.activities.MainActivity;
import com.example.personalfinance.databinding.FragmentDashboardBinding;
import com.example.personalfinance.models.Transaction;
import com.example.personalfinance.utils.Constants;
import com.example.personalfinance.utils.FirebaseHelper;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.text.NumberFormat;
import java.util.*;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private FirebaseHelper firebase;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebase = FirebaseHelper.getInstance();
        setHasOptionsMenu(true);

        String userName = firebase.getCurrentUser() != null
                ? firebase.getCurrentUser().getDisplayName() : "User";
        binding.tvGreeting.setText("Hello, " + (userName != null ? userName : "User") + " 👋");

        loadSummary();
    }

    private void loadSummary() {
        String userId = firebase.getCurrentUserId();
        if (userId == null) return;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        com.google.firebase.Timestamp startOfMonth = new com.google.firebase.Timestamp(cal.getTime());

        firebase.getDb().collection("transactions")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("date", startOfMonth)
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null || !isAdded()) return;

                    double income = 0, expense = 0;
                    Map<String, Double> categoryMap = new HashMap<>();

                    for (com.google.firebase.firestore.DocumentSnapshot doc : snap.getDocuments()) {
                        Transaction t = doc.toObject(Transaction.class);
                        if (t == null) continue;
                        if (Constants.TYPE_INCOME.equals(t.getType())) income += t.getAmount();
                        else {
                            expense += t.getAmount();
                            categoryMap.merge(t.getCategory(), t.getAmount(), Double::sum);
                        }
                    }

                    double finalIncome = income;
                    double finalExpense = expense;
                    binding.tvIncome.setText(currencyFormat.format(finalIncome));
                    binding.tvExpense.setText(currencyFormat.format(finalExpense));
                    binding.tvBalance.setText(currencyFormat.format(finalIncome - finalExpense));

                    setupPieChart(categoryMap);
                });
    }

    private void setupPieChart(Map<String, Double> data) {
        if (data.isEmpty()) {
            binding.pieChart.setVisibility(View.GONE);
            binding.tvNoData.setVisibility(View.VISIBLE);
            return;
        }
        binding.pieChart.setVisibility(View.VISIBLE);
        binding.tvNoData.setVisibility(View.GONE);

        List<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : data.entrySet()) {
            entries.add(new PieEntry(entry.getValue().floatValue(), entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Expenses");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setValueTextSize(12f);
        dataSet.setSliceSpace(3f);

        PieData pieData = new PieData(dataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.getDescription().setEnabled(false);
        binding.pieChart.setHoleRadius(40f);
        binding.pieChart.setCenterText("Expenses");
        binding.pieChart.animateY(1000);
        binding.pieChart.invalidate();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            ((MainActivity) requireActivity()).logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
