package com.example.mobileproject;
import com.example.mobileproject.ItemModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mobileproject.R;

import java.util.ArrayList;

public class ItemRVAdapter extends RecyclerView.Adapter<ItemRVAdapter.ViewHolder> {
    private ArrayList<ItemModel> ItemModelArrayList;
    private Context context;
    public ItemRVAdapter(ArrayList<ItemModel> ItemModelArrayList, Context context) {
        this.ItemModelArrayList = ItemModelArrayList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemModel modal = ItemModelArrayList.get(position);
        holder.itemNameTV.setText(modal.getItemName());
        holder.itemBarcodeTV.setText(modal.getItemBarcode());
        holder.itemCategoryTV.setText(modal.getItemCategory());
        holder.itemQuantityTV.setText(modal.getItemQuantity());
        holder.itemExpiryTV.setText(modal.getItemExpiry());
    }
    @Override
    public int getItemCount() {

        return ItemModelArrayList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView itemNameTV, itemBarcodeTV, itemCategoryTV, itemQuantityTV, itemExpiryTV;

        public ViewHolder(@NonNull View ViewDatabase) {
            super(ViewDatabase);
            itemNameTV = ViewDatabase.findViewById(R.id.idTVItemName);
            itemBarcodeTV = ViewDatabase.findViewById(R.id.idTVItemBarcode);
            itemCategoryTV = ViewDatabase.findViewById(R.id.idTVCategory);
            itemQuantityTV = ViewDatabase.findViewById(R.id.idTVQuantity);
            itemExpiryTV = ViewDatabase.findViewById(R.id.idTVExpiryDate);
        }
    }
}


