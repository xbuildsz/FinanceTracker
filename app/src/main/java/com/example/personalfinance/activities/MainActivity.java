package com.example.personalfinance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.personalfinance.R;
import com.example.personalfinance.databinding.ActivityMainBinding;
import com.example.personalfinance.fragments.BudgetFragment;
import com.example.personalfinance.fragments.DashboardFragment;
import com.example.personalfinance.fragments.TransactionsFragment;
import com.example.personalfinance.utils.FirebaseHelper;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupBottomNav();
        loadFragment(new DashboardFragment());

        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddTransactionActivity.class)));
    }

    private void setupBottomNav() {
        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment fragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) fragment = new DashboardFragment();
            else if (id == R.id.nav_transactions) fragment = new TransactionsFragment();
            else if (id == R.id.nav_budget) fragment = new BudgetFragment();

            if (fragment != null) {
                loadFragment(fragment);
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }

    public void logout() {
        FirebaseHelper.getInstance().getAuth().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finishAffinity();
    }
}
