package com.example.mobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "Kitchen_Items";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME_1 = "Items";
    private static final String ITEM_ID = "id";
    private static final String ITEM_NAME = "name";
    private static final String ITEM_BARCODE = "barcode";
    private static final String ITEM_CATEGORY = "category";
    private static final String ITEM_QUANTITY = "quantity";
    private static final String ITEM_EXPIRY = "expiry";

    private static final String TABLE_NAME_2 = "Waste";
    private static final String ITEM_ID_WASTE = "wid";
    private static final String ITEM_ID_FOREIGN = "fid";
    private static final String ITEM_NAME_WASTE = "name";
    private static final String ITEM_EXPIRY_WASTE = "expiry";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME_1 + " ("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_NAME + " TEXT,"
                + ITEM_BARCODE + " TEXT,"
                + ITEM_CATEGORY + " TEXT,"
                + ITEM_QUANTITY + " INTEGER,"
                + ITEM_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_NAME_2 + " ("
                + ITEM_ID_WASTE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_ID_FOREIGN + " INTEGER, "
                + ITEM_NAME_WASTE + " TEXT,"
                + ITEM_EXPIRY_WASTE + " TEXT, "
                + "FOREIGN KEY(" + ITEM_ID_FOREIGN + ") REFERENCES " + TABLE_NAME_1 + "(" + ITEM_ID + ") ON DELETE CASCADE)");
    }

    public void addNewItem(String itemName, String itemBarcode, String itemCategory, Integer quantity, String itemExpiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ITEM_NAME, itemName);
        values.put(ITEM_BARCODE, itemBarcode);
        values.put(ITEM_CATEGORY, itemCategory);
        values.put(ITEM_QUANTITY, quantity);
        values.put(ITEM_EXPIRY, itemExpiry);
        db.insert(TABLE_NAME_1, null, values);
        db.close();
    }

    public void moveExpiredItemsToWaste() {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME_1, null);

        if (cursor.moveToFirst()) {
            do {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME));
                String expiryDateStr = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_EXPIRY));

                LocalDate expiryDate = LocalDate.parse(expiryDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate today = LocalDate.now();

                if (!today.isBefore(expiryDate)) {
                    ContentValues wasteValues = new ContentValues();
                    wasteValues.put(ITEM_ID_FOREIGN, itemId);
                    wasteValues.put(ITEM_NAME_WASTE, itemName);
                    wasteValues.put(ITEM_EXPIRY_WASTE, expiryDateStr);

                    db.insert(TABLE_NAME_2, null, wasteValues);
                    db.delete(TABLE_NAME_1, ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        onCreate(db);
    }
    public Cursor getAllItemsCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Items", null);
    }

    public ArrayList<ItemModel> readItems() {
        ArrayList<ItemModel> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Items", null);

        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String barcode = cursor.getString(cursor.getColumnIndexOrThrow("barcode"));
                String category = cursor.getString(cursor.getColumnIndexOrThrow("category"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                String expiry = cursor.getString(cursor.getColumnIndexOrThrow("expiry"));

                items.add(new ItemModel(name, barcode, category, quantity, expiry));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return items;
    }

    public ArrayList<ItemModel> readWasteItems() {
        ArrayList<ItemModel> wasteItems = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Waste", null);

        if (cursor.moveToFirst()) {
            do {
                wasteItems.add(new ItemModel(
                        cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        "", "", 0,
                        cursor.getString(cursor.getColumnIndexOrThrow("expiry"))
                ));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return wasteItems;
    }

    public void insertDummyData() {
        if (readItems().isEmpty()) {
            // Expired Items (to demonstrate waste log)
            addNewItem("Old Milk", "11122233399", "Dairy", 1, "2025-07-09");
            addNewItem("Expired Yogurt", "11122233398", "Dairy", 2, "2025-07-10");
            addNewItem("Old Ground Beef", "11122233397", "Meat", 1, "2025-07-08");
            addNewItem("Stale Bread", "11122233396", "Bakery", 1, "2025-07-07");
            addNewItem("Wilted Lettuce", "11122233395", "Vegetables", 1, "2025-07-06");

            // Fresh Produce (short expiry: 1-5 days)
            addNewItem("Fresh Bananas", "11122233301", "Fruits", 6, "2025-07-15");
            addNewItem("Strawberries", "11122233302", "Fruits", 1, "2025-07-14");
            addNewItem("Spinach", "11122233303", "Vegetables", 1, "2025-07-13");
            addNewItem("Cherry Tomatoes", "11122233304", "Vegetables", 2, "2025-07-16");

            // Dairy Products & Deli (medium expiry: 15-45 days)
            addNewItem("Greek Yogurt", "11122233305", "Dairy", 2, "2025-07-25");
            addNewItem("Whole Milk", "11122233306", "Dairy", 1, "2025-07-20");
            addNewItem("Cheddar Cheese", "11122233307", "Dairy", 1, "2025-08-15");
            addNewItem("Hard Salami", "11122233330", "Deli", 1, "2025-08-01");
            addNewItem("Ham Slices", "11122233331", "Deli", 1, "2025-07-28");
            addNewItem("Cottage Cheese", "11122233332", "Dairy", 1, "2025-07-30");
            addNewItem("Cream Cheese", "11122233333", "Dairy", 2, "2025-08-05");
            addNewItem("Sliced Turkey", "11122233334", "Deli", 1, "2025-07-29");
            addNewItem("Swiss Cheese", "11122233335", "Dairy", 1, "2025-08-10");
            addNewItem("Butter", "11122233336", "Dairy", 2, "2025-08-20");
            addNewItem("Sour Cream", "11122233337", "Dairy", 1, "2025-08-02");

            // Fresh Meats (short expiry: 1-4 days)
            addNewItem("Chicken Breast", "11122233308", "Meat", 2, "2025-07-14");
            addNewItem("Ground Beef", "11122233309", "Meat", 1, "2025-07-13");
            addNewItem("Fresh Salmon", "11122233310", "Seafood", 2, "2025-07-12");

            // Bakery (short expiry: 3-7 days)
            addNewItem("Whole Wheat Bread", "11122233311", "Bakery", 1, "2025-07-15");
            addNewItem("Bagels", "11122233312", "Bakery", 4, "2025-07-14");

            // Semi-Perishables (medium expiry: 15-45 days)
            addNewItem("Fresh Pickles", "11122233338", "Condiments", 1, "2025-08-08");
            addNewItem("Hummus", "11122233339", "Dips", 2, "2025-07-28");
            addNewItem("Prepared Pesto", "11122233340", "Condiments", 1, "2025-08-01");
            addNewItem("Fresh Salsa", "11122233341", "Dips", 1, "2025-07-30");
            addNewItem("Tofu", "11122233342", "Proteins", 2, "2025-08-05");
            addNewItem("Egg Substitutes", "11122233343", "Dairy", 1, "2025-07-29");
            addNewItem("Fresh Pasta", "11122233344", "Ready Meals", 2, "2025-08-01");

            // Pantry Items (long expiry: 6+ months)
            addNewItem("Pasta", "11122233313", "Pantry", 2, "2025-12-31");
            addNewItem("Rice", "11122233314", "Grains", 1, "2025-12-15");
            addNewItem("Canned Tomatoes", "11122233315", "Canned", 3, "2026-06-30");

            // Frozen Items (long expiry: 3+ months)
            addNewItem("Frozen Peas", "11122233316", "Frozen", 2, "2026-01-15");
            addNewItem("Ice Cream", "11122233317", "Frozen", 1, "2025-09-30");

            // Beverages (medium expiry: 15-45 days)
            addNewItem("Orange Juice", "11122233318", "Beverages", 1, "2025-07-25");
            addNewItem("Almond Milk", "11122233319", "Beverages", 2, "2025-08-30");
            addNewItem("Oat Milk", "11122233345", "Beverages", 1, "2025-08-15");
            addNewItem("Cold Brew Coffee", "11122233346", "Beverages", 2, "2025-08-01");
            addNewItem("Kombucha", "11122233347", "Beverages", 3, "2025-07-28");

            // Condiments (long expiry)
            addNewItem("Olive Oil", "11122233320", "Pantry", 1, "2026-03-15");
            addNewItem("Soy Sauce", "11122233321", "Condiments", 1, "2026-06-30");
        }
    }
    public void deleteItem(String itemName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_1, ITEM_NAME + "=?", new String[]{itemName});
        db.close();
    }

    public void deleteAllItems() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME_1, null, null); // Delete all from Items table
        db.delete(TABLE_NAME_2, null, null); // Delete all from Waste table
        db.close();
    }


}
