package com.example.mobileproject;

public class ItemModel {

    private String itemName;
    private String itemBarcode;
    private String itemCategory;
    private Integer itemQuantity;
    private String itemExpiry;
    private int itemId;

    // creating getter and setter methods
    public String getItemName()
    { return itemName; }
    public void setItemName (String itemName)
    {
        this.itemName = itemName;
    }

    public String getItemBarcode()
    {
        return itemBarcode;
    }
    public void setItemBarcode(String itemBarcode)
    {
        this.itemBarcode = itemBarcode;
    }

    public String getItemCategory() { return itemCategory; }

    public void setItemCategory(String itemCategory)
    {
        this.itemCategory = itemCategory;
    }

    public Integer getItemQuantity()
    {
        return itemQuantity;
    }

    public void setItemQuantity(Integer itemQuantity)
    {
        this.itemQuantity = itemQuantity;
    }

    public String getItemExpiry()
    {
        return itemExpiry;
    }

    public void setItemExpiry(String itemExpiry)
    {
        this.itemExpiry = itemExpiry;
    }

    public int getItemId() { return itemId; }

    public void setItemId(int itemId) { this.itemId = itemId; }

    // constructor
    public ItemModel(String itemName,
                     String itemBarcode,
                     String itemCategory,
                     Integer itemQuantity,
                     String itemExpiry)
    {
        this.itemName = itemName;
        this.itemBarcode = itemBarcode;
        this.itemCategory = itemCategory;
        this.itemQuantity = itemQuantity;
        this.itemExpiry = itemExpiry;
    }
}

