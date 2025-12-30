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

    private static final String PREFS_NAME = "remember_me";
    private static final String KEY_EMAIL = "email";

    private static final String SESSION_PREFS = "session";
    private static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";

    private EditText emailInput, passwordInput;
    private Button loginButton;
    private TextView signupRedirect;
    private CheckBox rememberMe;
    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If user already logged in, skip login screen
        SharedPreferences session = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        String loggedInEmail = session.getString(KEY_LOGGED_IN_EMAIL, null);
        if (loggedInEmail != null && !loggedInEmail.trim().isEmpty()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.loginEmail);
        passwordInput = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        signupRedirect = findViewById(R.id.signupRedirect);
        rememberMe = findViewById(R.id.rememberMeCheckbox);

        db = new DBHelper(this);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String savedEmail = prefs.getString(KEY_EMAIL, "");
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
            // Save remember-me email if checked
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            if (rememberMe.isChecked()) {
                prefs.edit().putString(KEY_EMAIL, email).apply();
            } else {
                prefs.edit().remove(KEY_EMAIL).apply();
            }

            // Save session (current logged-in user)
            SharedPreferences session = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
            session.edit().putString(KEY_LOGGED_IN_EMAIL, email).apply();

            Toast.makeText(this, "Login successful !", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
