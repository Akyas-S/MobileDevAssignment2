package com.example.mobileproject;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewDatabase extends AppCompatActivity {

    private ArrayList<ItemModel> itemModelArrayList;
    private DBHandler dbHandler;
    private ItemRVAdapter itemRVAdapter;
    private RecyclerView itemRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_database);

        // initializing our all variables.
        itemModelArrayList = new ArrayList<>();
        dbHandler = new DBHandler(ViewDatabase.this);

        // getting our course array 
        // list from db handler class.
        itemModelArrayList = dbHandler.readItems();

        // on below line passing our array list to our adapter class.
        itemRVAdapter = new ItemRVAdapter(itemModelArrayList, ViewDatabase.this);
        itemRV = findViewById(R.id.idRVItems);

        // setting layout manager for our recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewDatabase.this, RecyclerView.VERTICAL, false);
        itemRV.setLayoutManager(linearLayoutManager);

        // setting our adapter to recycler view.
        itemRV.setAdapter(itemRVAdapter);
    }
}