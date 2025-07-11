package com.example.mobileproject;

public class ItemModel {
    private String name;
    private String barcode;
    private String category;
    private int quantity;
    private String expiry;

    public ItemModel(String name, String barcode, String category, int quantity, String expiry) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.quantity = quantity;
        this.expiry = expiry;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getExpiry() {
        return expiry;
    }
}
