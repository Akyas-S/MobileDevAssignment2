package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import android.app.AlertDialog;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;

public class ProfileActivity extends AppCompatActivity {
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHandler = new DBHandler(this);

        MaterialButton deleteAllButton = findViewById(R.id.deleteAllButton);
        MaterialButton addDummyDataButton = findViewById(R.id.addDummyDataButton);

        deleteAllButton.setOnClickListener(v -> showDeleteConfirmationDialog());
        addDummyDataButton.setOnClickListener(v -> showAddDummyDataConfirmationDialog());

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.wastelog) {
                startActivity(new Intent(this, WasteLogActivity.class));
                return true;
            } else if (id == R.id.additem) {
                startActivity(new Intent(this, AddItem.class));
                return true;
            } else if (id == R.id.recepie) {
                startActivity(new Intent(this, RecepieActivity.class));
                return true;
            } else if (id == R.id.profile) {
                // Already in ProfileActivity
                return true;
            }
            return false;
        });
    }

    private void showAddDummyDataConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Add Sample Data")
                .setMessage("This will add sample items to your inventory. Continue?")
                .setPositiveButton("Add", (dialog, which) -> {
                    dbHandler.insertDummyData();
                    Toast.makeText(this, "Sample data added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Delete All Items")
                .setMessage("Are you sure you want to delete all items and waste log entries? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHandler.deleteAllItems();
                    Toast.makeText(this, "All items and waste log cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
