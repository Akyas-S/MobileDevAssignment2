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



}
