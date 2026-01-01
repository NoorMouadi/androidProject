package com.example.a1211080_1210345_projectcourse.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.a1211080_1210345_projectcourse.R;
import com.example.a1211080_1210345_projectcourse.database.DBHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class IncomeFragment extends Fragment {

    private static final String SESSION_PREFS = "session";
    private static final String KEY_LOGGED_IN_EMAIL = "logged_in_email";

    private DBHelper db;
    private String userEmail;

    private Button btnAddIncome;
    private ListView listIncome;

    // We store ids separately so we can delete the correct row
    private final ArrayList<Integer> trxIds = new ArrayList<>();
    private final ArrayList<String> trxDisplay = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    public IncomeFragment() { }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_income, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = new DBHelper(requireContext());

        btnAddIncome = view.findViewById(R.id.btnAddIncome);
        listIncome = view.findViewById(R.id.listIncome);

        userEmail = getLoggedInEmail(requireContext());
        if (userEmail == null) {
            Toast.makeText(requireContext(), "Session expired. Please login again.", Toast.LENGTH_SHORT).show();
            return;
        }

        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, trxDisplay);
        listIncome.setAdapter(adapter);

        loadIncomeList();

        btnAddIncome.setOnClickListener(v -> showAddDialog());

        // Long press to delete
        listIncome.setOnItemLongClickListener((parent, v, position, id) -> {
            int trxId = trxIds.get(position);
            confirmDelete(trxId);
            return true;
        });
    }

    private String getLoggedInEmail(Context context) {
        SharedPreferences session = context.getSharedPreferences(SESSION_PREFS, Context.MODE_PRIVATE);
        String email = session.getString(KEY_LOGGED_IN_EMAIL, null);
        if (email != null && !email.trim().isEmpty()) return email.trim();
        return null;
    }

    private void loadIncomeList() {
        trxIds.clear();
        trxDisplay.clear();

        Cursor c = db.getTransactionsForUser(userEmail, "income");
        if (c != null) {
            while (c.moveToNext()) {
                int id = c.getInt(c.getColumnIndexOrThrow("id"));
                double amount = c.getDouble(c.getColumnIndexOrThrow("amount"));
                String date = c.getString(c.getColumnIndexOrThrow("date"));
                String desc = c.getString(c.getColumnIndexOrThrow("description"));

                trxIds.add(id);
                String line = date + " | +" + amount + " | " + (desc == null ? "" : desc);
                trxDisplay.add(line);
            }
            c.close();
        }

        adapter.notifyDataSetChanged();
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Add Income");

        // Dialog layout
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad, pad, pad);

        EditText amountInput = new EditText(requireContext());
        amountInput.setHint("Amount");
        amountInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

        EditText descInput = new EditText(requireContext());
        descInput.setHint("Description (optional)");

        layout.addView(amountInput);
        layout.addView(descInput);

        builder.setView(layout);

        builder.setPositiveButton("Save", (dialog, which) -> {
            String amountStr = amountInput.getText().toString().trim();
            String desc = descInput.getText().toString().trim();

            if (amountStr.isEmpty()) {
                Toast.makeText(requireContext(), "Amount is required", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount;
            try {
                amount = Double.parseDouble(amountStr);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                return;
            }

            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

            long res = db.insertTransaction(userEmail, "income", amount, today, null, desc);
            if (res != -1) {
                Toast.makeText(requireContext(), "Income added", Toast.LENGTH_SHORT).show();
                loadIncomeList();
            } else {
                Toast.makeText(requireContext(), "Failed to add income", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void confirmDelete(int trxId) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete")
                .setMessage("Delete this income transaction?")
                .setPositiveButton("Delete", (d, w) -> {
                    boolean ok = db.deleteTransaction(trxId);
                    if (ok) {
                        Toast.makeText(requireContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        loadIncomeList();
                    } else {
                        Toast.makeText(requireContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
