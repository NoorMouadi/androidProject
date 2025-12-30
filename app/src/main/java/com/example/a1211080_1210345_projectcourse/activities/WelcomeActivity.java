package com.example.a1211080_1210345_projectcourse.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import com.example.a1211080_1210345_projectcourse.MainActivity;
import com.example.a1211080_1210345_projectcourse.R;

public class WelcomeActivity extends AppCompatActivity {

    private static final String SESSION_PREFS = "session";
    private static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_welcome);
        SharedPreferences session = getSharedPreferences(SESSION_PREFS, MODE_PRIVATE);
        String loggedInEmail = session.getString(KEY_LOGGED_IN_EMAIL, null);

        if (loggedInEmail != null && !loggedInEmail.trim().isEmpty()) {
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
