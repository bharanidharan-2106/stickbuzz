package com.stickbuzz.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuthException;
import com.stickbuzz.R;
import com.stickbuzz.ui.admin.AdminDashboardActivity;
import com.stickbuzz.ui.user.UserDashboardActivity;
import com.stickbuzz.utils.FirebaseUtil;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailLayout, passwordLayout;
    private TextInputEditText etEmail, etPassword;
    private ProgressBar progressBar;
    private AuthViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initViews();
        highlightDescription();
        setupListeners();

        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        observeViewModel();
    }

    private void initViews() {
        emailLayout = findViewById(R.id.emailLayout);
        passwordLayout = findViewById(R.id.passwordLayout);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        progressBar = findViewById(R.id.progressBar);
    }

    private void highlightDescription() {
        TextView txtDescription = findViewById(R.id.txtDescription);

        String desc = "StickBuzz provides Live hockey scores, match updates, results and tournament insights in real time.\n\nPlease Login or Register to continue.";

        SpannableString spannable = new SpannableString(desc);

        int loginStart = desc.indexOf("Login");
        int registerStart = desc.indexOf("Register");

        spannable.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.primaryOrange)),
                loginStart,
                loginStart + 5,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new ForegroundColorSpan(getResources().getColor(R.color.primaryOrange)),
                registerStart,
                registerStart + 8,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        txtDescription.setText(spannable);
    }

    private void setupListeners() {

        findViewById(R.id.txtRegister).setOnClickListener(v ->
                startActivity(new Intent(this, RegisterActivity.class)));

        findViewById(R.id.txtForgot).setOnClickListener(v -> handleForgotPassword());

        findViewById(R.id.btnLogin).setOnClickListener(v -> handleLogin());
    }

    private void handleLogin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        emailLayout.setError(null);
        passwordLayout.setError(null);

        viewModel.clearError();

        if (email.isEmpty()) {
            emailLayout.setError("Email is required");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Password is required");
            return;
        }

        viewModel.login(email, password);
    }

    private void handleForgotPassword() {

        String email = etEmail.getText().toString().trim();

        emailLayout.setError(null);

        if (email.isEmpty()) {
            emailLayout.setError("Enter your registered email to reset password");
            return;
        }

        FirebaseUtil.getAuth()
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(unused ->
                        emailLayout.setHelperText("Reset link sent to your email. Check inbox or spam folder."))
                .addOnFailureListener(e ->
                        emailLayout.setError("Email not found. Kindly register."));
    }

    private void observeViewModel() {

        viewModel.loadingLiveData.observe(this,
                loading -> progressBar.setVisibility(
                        loading ? View.VISIBLE : View.GONE));

        viewModel.roleLiveData.observe(this, role -> {

            if (role == null) return;

            if (role.equals("admin"))
                startActivity(new Intent(this, AdminDashboardActivity.class));
            else
                startActivity(new Intent(this, UserDashboardActivity.class));

            finish();
        });

        viewModel.errorLiveData.observe(this, error -> {

            if (error == null) return;

            if (error.equals("INVALID_PASSWORD")) {
                passwordLayout.setError("Invalid password");
            }
            else if (error.equals("USER_NOT_FOUND")) {
                emailLayout.setError("Email not found. Kindly register.");
            }
            else if (error.equals("INVALID_EMAIL_FORMAT")) {
                emailLayout.setError("Invalid email format");
            }
            else {
                emailLayout.setError("Authentication failed. Try again.");
            }
        });
    }
}