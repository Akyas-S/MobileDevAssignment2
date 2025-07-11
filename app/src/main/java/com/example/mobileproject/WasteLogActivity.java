package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class WasteLogActivity extends AppCompatActivity {
    private ArrayList<ItemModel> wasteItemsList;
    private DBHandler dbHandler;
    private WasteLogAdapter wasteLogAdapter;
    private RecyclerView wasteRV;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_waste_log);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database
        dbHandler = new DBHandler(this);

        // Initialize views
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        wasteRV = findViewById(R.id.wasteRecyclerView);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        wasteItemsList = new ArrayList<>();
        wasteRV.setHasFixedSize(true);
        wasteRV.setLayoutManager(new LinearLayoutManager(this));
        wasteLogAdapter = new WasteLogAdapter(wasteItemsList, this);
        wasteRV.setAdapter(wasteLogAdapter);
        refreshData();
    }

    private void refreshData() {
        wasteItemsList.clear();
        wasteItemsList.addAll(dbHandler.readWasteItems());
        wasteLogAdapter.notifyDataSetChanged();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.wastelog);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.wastelog) {
                return true;
            } else if (id == R.id.additem) {
                startActivity(new Intent(this, AddItem.class));
                return true;
            } else if (id == R.id.recepie) {
                startActivity(new Intent(this, RecepieActivity.class));
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}
