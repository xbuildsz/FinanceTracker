package com.example.personalfinance.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.personalfinance.R;
import com.example.personalfinance.activities.TransactionDetailActivity;
import com.example.personalfinance.models.Transaction;
import com.example.personalfinance.utils.Constants;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private final List<Transaction> transactions;
    private final Context context;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

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
        CATEGORY_EMOJI.put("Salary", "💼");
        CATEGORY_EMOJI.put("Freelance", "💻");
        CATEGORY_EMOJI.put("Business", "📊");
        CATEGORY_EMOJI.put("Investment", "📈");
        CATEGORY_EMOJI.put("Gift", "🎁");
        CATEGORY_EMOJI.put("Other", "💰");
    }

    public TransactionAdapter(List<Transaction> transactions, Context context) {
        this.transactions = transactions;
        this.context = context;
    }

    @NonNull @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction t = transactions.get(position);

        holder.tvTitle.setText(t.getTitle());
        holder.tvCategory.setText(t.getCategory() != null ? t.getCategory() : "");

        String emoji = CATEGORY_EMOJI.getOrDefault(t.getCategory(), "💰");
        holder.tvCategoryIcon.setText(emoji);

        if (t.getDate() != null) {
            holder.tvDate.setText(dateFormat.format(t.getDate().toDate()));
        }

        boolean isIncome = Constants.TYPE_INCOME.equals(t.getType());
        String prefix = isIncome ? "+ " : "- ";
        holder.tvAmount.setText(prefix + currencyFormat.format(t.getAmount()));
        holder.tvAmount.setTextColor(context.getColor(isIncome ? R.color.colorIncome : R.color.colorExpense));

        // Show receipt indicator
        holder.ivReceipt.setVisibility(
                t.getReceiptUrl() != null && !t.getReceiptUrl().isEmpty()
                        ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, TransactionDetailActivity.class);
            intent.putExtra("transactionId", t.getId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return transactions.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryIcon, tvTitle, tvCategory, tvDate, tvAmount;
        ImageView ivReceipt;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryIcon = itemView.findViewById(R.id.tvCategoryIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            ivReceipt = itemView.findViewById(R.id.ivReceipt);
        }
    }
}
