package com.stickbuzz.ui.auth;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.stickbuzz.R;

import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout nameLayout, emailLayout, passwordLayout, confirmLayout;
    private TextInputEditText etName, etEmail, etPassword, etConfirm;
    private Button btnRegister;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    private final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[@#$%^&+=!]).{8,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        btnRegister.setOnClickListener(v -> validateAndRegister());

        viewModel.loadingLiveData.observe(this,
                loading -> progressBar.setVisibility(
                        loading ? View.VISIBLE : View.GONE));

        viewModel.roleLiveData.observe(this, role -> {
            if (role != null) {
                finish();
            }
        });
    }

    private void initViews() {
        nameLayout = findViewById(R.id.nameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);
        confirmLayout = findViewById(R.id.confirmLayout);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
    }

    private void validateAndRegister() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();
        String confirm = etConfirm.getText().toString().trim();

        clearErrors();

        if (name.isEmpty()) {
            nameLayout.setError("Name is required");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Enter valid email (must contain @)");
            return;
        }

        if (!PASSWORD_PATTERN.matcher(pass).matches()) {
            passwordLayout.setError(
                    "Password must be 8+ chars, 1 Capital, 1 Number, 1 Special");
            return;
        }

        if (!pass.equals(confirm)) {
            confirmLayout.setError("Passwords do not match");
            return;
        }

        viewModel.register(name, email, pass);
    }

    private void clearErrors() {
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmLayout.setError(null);
    }
}