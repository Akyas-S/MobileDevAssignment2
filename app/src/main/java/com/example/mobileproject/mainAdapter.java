package com.example.mobileproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class mainAdapter extends RecyclerView.Adapter<mainAdapter.MyViewHolder> {
    private Context context;
    DBHandler dbHandler;
    private ArrayList<ItemModel> ItemModelArrayList;
    public mainAdapter(ArrayList<ItemModel> ItemModelArrayList, Context context) {
        this.ItemModelArrayList = ItemModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.userentry,parent,false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ItemModel modal = ItemModelArrayList.get(position);
        holder.name_id.setText(modal.getItemName());
        //holder.itemBarcodeTV.setText(modal.getItemBarcode());
        holder.quantity_id.setText(modal.getItemQuantity());
        holder.expiry_id.setText(modal.getItemExpiry());
    }

    @Override
    public int getItemCount() {
        return ItemModelArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name_id, quantity_id, expiry_id;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name_id = itemView.findViewById(R.id.textname);
            quantity_id = itemView.findViewById(R.id.textemail);
            expiry_id = itemView.findViewById(R.id.textage);
        }

    }
}
