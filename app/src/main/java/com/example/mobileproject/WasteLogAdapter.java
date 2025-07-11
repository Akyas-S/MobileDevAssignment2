package com.example.mobileproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WasteLogAdapter extends RecyclerView.Adapter<WasteLogAdapter.ViewHolder> {
    private ArrayList<ItemModel> wasteItemsList;
    private Context context;

    public WasteLogAdapter(ArrayList<ItemModel> wasteItemsList, Context context) {
        this.wasteItemsList = wasteItemsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waste_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel item = wasteItemsList.get(position);
        holder.itemNameTV.setText("Item: " + item.getItemName());
        holder.itemExpiryTV.setText("Expired on: " + item.getItemExpiry());
    }

    @Override
    public int getItemCount() {
        return wasteItemsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTV, itemExpiryTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameTV = itemView.findViewById(R.id.idTVWasteItemName);
            itemExpiryTV = itemView.findViewById(R.id.idTVWasteExpiryDate);
        }
    }
}
