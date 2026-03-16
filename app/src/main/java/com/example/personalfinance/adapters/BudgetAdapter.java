package com.example.personalfinance.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinance.R;
import com.example.personalfinance.models.Budget;
import com.example.personalfinance.utils.FirebaseHelper;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.ViewHolder> {

    private final List<Budget> budgets;
    private final Context context;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    private static final Map<String, String> CATEGORY_EMOJI = new HashMap<>();
    static {
        CATEGORY_EMOJI.put("Food & Dining", "🍔");
        CATEGORY_EMOJI.put("Shopping", "🛍️");
        CATEGORY_EMOJI.put("Transportation", "🚗");
        CATEGORY_EMOJI.put("Housing", "🏠");
        CATEGORY_EMOJI.put("Healthcare", "💊");
        CATEGORY_EMOJI.put("Entertainment", "🎬");
        CATEGORY_EMOJI.put("Education", "📚");
        CATEGORY_EMOJI.put("Travel", "✈️");
        CATEGORY_EMOJI.put("Bills & Utilities", "⚡");
        CATEGORY_EMOJI.put("Personal Care", "💄");
        CATEGORY_EMOJI.put("Other", "💰");
    }

    public BudgetAdapter(List<Budget> budgets, Context context) {
        this.budgets = budgets;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_budget, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Budget b = budgets.get(position);

        holder.tvCategory.setText(b.getCategory());
        holder.tvCategoryIcon.setText(CATEGORY_EMOJI.getOrDefault(b.getCategory(), "💰"));
        holder.tvSpentOf.setText(currencyFormat.format(b.getSpent()) + " of " + currencyFormat.format(b.getLimit()));

        int percent = (int) Math.min(b.getPercentage(), 100);
        holder.progressBudget.setProgress(percent);
        holder.tvPercent.setText(percent + "%");

        double remaining = b.getRemaining();
        if (remaining >= 0) {
            holder.tvRemaining.setText(currencyFormat.format(remaining) + " left");
            holder.tvRemaining.setTextColor(context.getColor(R.color.colorIncome));
            holder.tvPercent.setTextColor(context.getColor(
                    percent >= 90 ? R.color.colorExpense : R.color.colorTextPrimary));
        } else {
            holder.tvRemaining.setText("Over by " + currencyFormat.format(Math.abs(remaining)));
            holder.tvRemaining.setTextColor(context.getColor(R.color.colorExpense));
            holder.tvPercent.setTextColor(context.getColor(R.color.colorExpense));
        }

        // Delete button
        holder.tvDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Budget")
                    .setMessage("Delete budget for " + b.getCategory() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        if (b.getId() != null) {
                            FirebaseHelper.getInstance().deleteBudget(b.getId(),
                                    new FirebaseHelper.OnCompleteListener<Void>() {
                                        @Override
                                        public void onSuccess(Void result) {
                                            Toast.makeText(context, "Budget deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                        @Override
                                        public void onFailure(Exception e) {
                                            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() { return budgets.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryIcon, tvCategory, tvSpentOf, tvPercent, tvRemaining, tvDelete;
        ProgressBar progressBudget;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvSpentOf = itemView.findViewById(R.id.tvSpentOf);
            tvPercent = itemView.findViewById(R.id.tvPercent);
            tvRemaining = itemView.findViewById(R.id.tvRemaining);
            tvDelete = itemView.findViewById(R.id.tvDelete);
            progressBudget = itemView.findViewById(R.id.progressBudget);
        }
    }
}