package com.example.mobileproject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

public class mainAdapter extends RecyclerView.Adapter<mainAdapter.ViewHolder> {
    private ArrayList<ItemModel> ItemModelArrayList;
    private Context context;
    private DBHandler dbHandler;

    public mainAdapter(ArrayList<ItemModel> ItemModelArrayList, Context context) {
        this.ItemModelArrayList = ItemModelArrayList;
        this.context = context;
        this.dbHandler = new DBHandler(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel modal = ItemModelArrayList.get(position);
        holder.itemNameTV.setText(modal.getItemName());
        holder.itemCategoryTV.setText(modal.getItemCategory());
        holder.itemQuantityTV.setText(String.valueOf(modal.getItemQuantity()));
        holder.itemExpiryTV.setText(modal.getItemExpiry());

        // Calculate expiry progress
        LocalDate today = LocalDate.now();
        LocalDate expiryDate = LocalDate.parse(modal.getItemExpiry());
        long daysUntilExpiry = ChronoUnit.DAYS.between(today, expiryDate);

        // Set progress based on days until expiry
        int progress = (int) ((daysUntilExpiry / 60.0) * 100);
        progress = Math.min(100, Math.max(0, progress));
        holder.expiryProgress.setProgress(progress);

        if (progress <= 25) {
            holder.expiryProgress.setProgressTintList(ColorStateList.valueOf(Color.RED));
        } else if (progress <= 50) {
            holder.expiryProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#FFA500")));
        } else {
            holder.expiryProgress.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#15F595")));
        }

        holder.deleteButton.setOnClickListener(v -> {
            int currentPosition = holder.getAdapterPosition();
            if (currentPosition != RecyclerView.NO_POSITION) {
                showDeleteConfirmationDialog(currentPosition, modal.getItemName());
            }
        });
    }

    private void showDeleteConfirmationDialog(int position, String itemName) {
        new AlertDialog.Builder(context)
                .setTitle("Delete Item")
                .setMessage("Are you sure you want to delete '" + itemName + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    dbHandler.deleteItem(itemName);
                    ItemModelArrayList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return ItemModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView itemNameTV, itemCategoryTV, itemQuantityTV, itemExpiryTV;
        private ProgressBar expiryProgress;
        private MaterialButton deleteButton;

        public ViewHolder(@NonNull View ViewDatabase) {
            super(ViewDatabase);
            itemNameTV = ViewDatabase.findViewById(R.id.idTVItemName);
            itemCategoryTV = ViewDatabase.findViewById(R.id.idTVCategory);
            itemQuantityTV = ViewDatabase.findViewById(R.id.idTVQuantity);
            itemExpiryTV = ViewDatabase.findViewById(R.id.idTVExpiryDate);
            expiryProgress = ViewDatabase.findViewById(R.id.expiryProgress);
            deleteButton = ViewDatabase.findViewById(R.id.deleteButton);
        }
    }
}