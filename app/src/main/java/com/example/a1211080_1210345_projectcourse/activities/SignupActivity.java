package com.example.a1211080_1210345_projectcourse.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a1211080_1210345_projectcourse.R;
import com.example.a1211080_1210345_projectcourse.database.DBHelper;

import java.util.regex.Pattern;

public class SignupActivity extends AppCompatActivity {

    EditText emailInput, firstNameInput, lastNameInput, passwordInput, confirmPasswordInput;
    Button signupButton;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailInput = findViewById(R.id.emailInput);
        firstNameInput = findViewById(R.id.firstNameInput);
        lastNameInput = findViewById(R.id.lastNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        signupButton = findViewById(R.id.signupButton);

        db = new DBHelper(this);
        attachAutoReset(emailInput);
        attachAutoReset(firstNameInput);
        attachAutoReset(lastNameInput);
        attachAutoReset(passwordInput);
        attachAutoReset(confirmPasswordInput);

        signupButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {

        resetErrors();

        String email = emailInput.getText().toString().trim();
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();

        boolean valid = true;

        // I. Email validation
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showError(emailInput, "Invalid email format");
            valid = false;
        }

        // II. First name validation
        if (firstName.length() < 3 || firstName.length() > 10) {
            showError(firstNameInput, "First name must be 3–10 characters");
            valid = false;
        }

        // III. Last name validation
        if (lastName.length() < 3 || lastName.length() > 10) {
            showError(lastNameInput, "Last name must be 3–10 characters");
            valid = false;
        }

        // IV. Password validation
        if (!isValidPassword(password)) {
            showError(passwordInput,
                    "Password must be 6–12 chars, include upper, lower, and number");
            valid = false;
        }

        // V. Confirm password
        if (!password.equals(confirmPassword)) {
            showError(confirmPasswordInput, "Passwords do not match");
            valid = false;
        }

        if (!valid) {
            Toast.makeText(this, "Please fix highlighted errors", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if user already exists
        if (db.userExists(email)) {
            showError(emailInput, "Email already registered");
            Toast.makeText(this, "User already exists", Toast.LENGTH_SHORT).show();
            return;
        }

        // Register user
        boolean inserted = db.insertUser(email, firstName, lastName, password);

        if (inserted) {
            Toast.makeText(this, "Registration successful", Toast.LENGTH_LONG).show();
            finish(); // return to login
        } else {
            Toast.makeText(this, "Registration failed", Toast.LENGTH_LONG).show();
        }
    }

    // Helper methods

    private void showError(EditText field, String message) {
        field.setError(message);
        field.setTextColor(Color.RED);
    }

    private void resetErrors() {
        resetField(emailInput);
        resetField(firstNameInput);
        resetField(lastNameInput);
        resetField(passwordInput);
        resetField(confirmPasswordInput);
    }

    private void resetField(EditText field) {
        field.setError(null);
        field.setTextColor(Color.BLACK);
    }
    
    private void attachAutoReset(EditText field) {
    field.addTextChangedListener(new android.text.TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            field.setError(null);
            field.setTextColor(Color.BLACK);
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    });
}

    private boolean isValidPassword(String password) {
        if (password.length() < 6 || password.length() > 12)
            return false;

        Pattern upper = Pattern.compile("[A-Z]");
        Pattern lower = Pattern.compile("[a-z]");
        Pattern digit = Pattern.compile("[0-9]");

        return upper.matcher(password).find()
                && lower.matcher(password).find()
                && digit.matcher(password).find();
    }
}
