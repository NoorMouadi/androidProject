package com.example.a1211080_1210345_projectcourse.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.a1211080_1210345_projectcourse.MainActivity;
import com.example.a1211080_1210345_projectcourse.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean("logged_in", false);

        if (loggedIn) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }
        findViewById(R.id.loginBtn).setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)));

        findViewById(R.id.signupBtn).setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }
}