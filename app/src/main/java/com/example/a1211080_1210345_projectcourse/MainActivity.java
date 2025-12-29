package com.example.a1211080_1210345_projectcourse;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.a1211080_1210345_projectcourse.activities.LoginActivity;
import com.example.a1211080_1210345_projectcourse.fragments.BudgetFragment;
import com.example.a1211080_1210345_projectcourse.fragments.ExpensesFragment;
import com.example.a1211080_1210345_projectcourse.fragments.HomeFragment;
import com.example.a1211080_1210345_projectcourse.fragments.IncomeFragment;
import com.example.a1211080_1210345_projectcourse.fragments.ProfileFragment;
import com.example.a1211080_1210345_projectcourse.fragments.ReportsFragment;
import com.example.a1211080_1210345_projectcourse.fragments.SettingsFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 🔴 THIS WAS MISSING
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Default fragment
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();

            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        Fragment fragment = null;

        if (item.getItemId() == R.id.nav_home) {
            fragment = new HomeFragment();

        } else if (item.getItemId() == R.id.nav_income) {
            fragment = new IncomeFragment();

        } else if (item.getItemId() == R.id.nav_expenses) {
            fragment = new ExpensesFragment();

        } else if (item.getItemId() == R.id.nav_budget) {
            fragment = new BudgetFragment();

        } else if (item.getItemId() == R.id.nav_reports) {
            fragment = new ReportsFragment();

        } else if (item.getItemId() == R.id.nav_profile) {
            fragment = new ProfileFragment();

        } else if (item.getItemId() == R.id.nav_settings) {
            fragment = new SettingsFragment();

        } else if (item.getItemId() == R.id.nav_logout) {
            SharedPreferences prefs = getSharedPreferences("user_session", MODE_PRIVATE);
            prefs.edit().clear().apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }

        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }

        drawerLayout.closeDrawers();
        return true;
    }
}
