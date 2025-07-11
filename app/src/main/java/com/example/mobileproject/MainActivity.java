package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private ArrayList<ItemModel> itemModelArrayList;
    private DBHandler dbHandler;
    private mainAdapter itemRVAdapter;
    private RecyclerView itemRV;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MaterialButton sortButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database
        dbHandler = new DBHandler(this);

        // Initialize views
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        sortButton = findViewById(R.id.sortButton);
        itemRV = findViewById(R.id.recyclerView);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
            swipeRefreshLayout.setRefreshing(false);
        });

        // Setup sort button
        sortButton.setOnClickListener(v -> showSortMenu());

        // Setup bottom navigation
        setupBottomNavigation();
    }

    private void setupRecyclerView() {
        itemModelArrayList = new ArrayList<>();
        itemRV.setHasFixedSize(true);
        itemRV.setLayoutManager(new LinearLayoutManager(this));
        itemRVAdapter = new mainAdapter(itemModelArrayList, this);
        itemRV.setAdapter(itemRVAdapter);
        refreshData();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.home);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
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
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void showSortMenu() {
        PopupMenu popup = new PopupMenu(MainActivity.this, sortButton);
        popup.getMenuInflater().inflate(R.menu.sort_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.sort_name) {
                    Collections.sort(itemModelArrayList, (item1, item2) ->
                            item1.getItemName().compareToIgnoreCase(item2.getItemName()));
                    itemRVAdapter.notifyDataSetChanged();
                    return true;
                } else if (id == R.id.sort_expiry) {
                    Collections.sort(itemModelArrayList, (item1, item2) ->
                            item1.getItemExpiry().compareTo(item2.getItemExpiry()));
                    itemRVAdapter.notifyDataSetChanged();
                    return true;
                } else if (id == R.id.sort_category) {
                    Collections.sort(itemModelArrayList, (item1, item2) ->
                            item1.getItemCategory().compareToIgnoreCase(item2.getItemCategory()));
                    itemRVAdapter.notifyDataSetChanged();
                    return true;
                } else if (id == R.id.sort_quantity) {
                    Collections.sort(itemModelArrayList, (item1, item2) ->
                            item2.getItemQuantity().compareTo(item1.getItemQuantity()));
                    itemRVAdapter.notifyDataSetChanged();
                    return true;
                }
                return false;
            }
        });

        popup.show();
    }

    private void refreshData() {
        itemModelArrayList.clear();
        itemModelArrayList.addAll(dbHandler.readItems());
        itemRVAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }
}