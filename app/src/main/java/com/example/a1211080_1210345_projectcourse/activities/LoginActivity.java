package com.example.a1211080_1210345_projectcourse.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a1211080_1210345_projectcourse.MainActivity;
import com.example.a1211080_1210345_projectcourse.R;
import com.example.a1211080_1210345_projectcourse.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput, passwordInput;
    Button loginButton;
    TextView signupRedirect;
    CheckBox rememberMe;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect = findViewById(R.id.signupRedirect);
        rememberMe = findViewById(R.id.rememberMeCheckbox);

        db = new DBHelper(this);

        // ✅ Prefill remembered email
        SharedPreferences prefs = getSharedPreferences("remember_me", MODE_PRIVATE);
        String savedEmail = prefs.getString("email", "");

        if (!savedEmail.isEmpty()) {
            emailInput.setText(savedEmail);
            rememberMe.setChecked(true);
        }

        loginButton.setOnClickListener(v -> loginUser());

        signupRedirect.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class))
        );
    }

    private void loginUser() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        boolean valid = true;

        if (email.isEmpty()) {
            emailInput.setError("Email required");
            valid = false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Password required");
            valid = false;
        }

        if (!valid) return;

        boolean success = db.checkUserLogin(email, password);

        if (success) {

            SharedPreferences prefs = getSharedPreferences("remember_me", MODE_PRIVATE);

            if (rememberMe.isChecked()) {
                prefs.edit().putString("email", email).apply();
            } else {
                prefs.edit().remove("email").apply();
            }

            Toast.makeText(this, "Login successful ☕", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
