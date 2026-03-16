package com.example.personalfinance.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.personalfinance.databinding.ActivityRegisterBinding;
import com.example.personalfinance.utils.FirebaseHelper;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private FirebaseHelper firebase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebase = FirebaseHelper.getInstance();

        binding.btnRegister.setOnClickListener(v -> registerUser());
        binding.tvLogin.setOnClickListener(v -> finish());
    }

    private void registerUser() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPassword = binding.etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name)) { binding.etName.setError("Name required"); return; }
        if (TextUtils.isEmpty(email)) { binding.etEmail.setError("Email required"); return; }
        if (TextUtils.isEmpty(password)) { binding.etPassword.setError("Password required"); return; }
        if (password.length() < 6) { binding.etPassword.setError("Min 6 characters"); return; }
        if (!password.equals(confirmPassword)) {
            binding.etConfirmPassword.setError("Passwords do not match");
            return;
        }

        setLoading(true);
        firebase.getAuth().createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(result -> {
                    // Update display name
                    result.getUser().updateProfile(
                            new com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(name).build()
                    ).addOnCompleteListener(task -> {
                        setLoading(false);
                        startActivity(new Intent(this, MainActivity.class));
                        finishAffinity();
                    });
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void setLoading(boolean loading) {
        binding.btnRegister.setEnabled(!loading);
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
    }
}
