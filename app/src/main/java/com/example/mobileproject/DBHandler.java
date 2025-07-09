package com.example.mobileproject;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import android.database.Cursor;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class DBHandler extends SQLiteOpenHelper {
    private static final String DB_NAME = "Kitchen Items";
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
        String createItemsTable = "CREATE TABLE " + TABLE_NAME_1 + " ("
                + ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_NAME + " TEXT,"
                + ITEM_BARCODE + " TEXT,"
                + ITEM_CATEGORY + " TEXT,"
                + ITEM_QUANTITY + " INTEGER,"
                + ITEM_EXPIRY + " TEXT)";
        db.execSQL(createItemsTable);

        String createWasteTable = "CREATE TABLE " + TABLE_NAME_2 + " ("
                + ITEM_ID_WASTE + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ITEM_ID_FOREIGN + " INTEGER, "
                + ITEM_NAME_WASTE + " TEXT,"
                + ITEM_EXPIRY_WASTE + " TEXT, "
                + "FOREIGN KEY(" + ITEM_ID_FOREIGN + ") REFERENCES " + TABLE_NAME_1 + "(" + ITEM_ID + ") ON DELETE CASCADE)";
        db.execSQL(createWasteTable);
    }
    public void addNewItem(String itemName, String itemBarcode, String itemCategory, Integer quantity, String itemExpiry) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues itemValues = new ContentValues();
        itemValues.put(ITEM_NAME, itemName);
        itemValues.put(ITEM_BARCODE, itemBarcode);
        itemValues.put(ITEM_CATEGORY, itemCategory);
        itemValues.put(ITEM_QUANTITY, quantity);
        itemValues.put(ITEM_EXPIRY, itemExpiry);
        db.insert(TABLE_NAME_1, null, itemValues);

        db.close();
    }

    public ArrayList<ItemModel> readItems() {

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorItems = db.rawQuery("SELECT * FROM " + TABLE_NAME_1, null);

        ArrayList<ItemModel> itemModelArrayList = new ArrayList<>();

        if (cursorItems.moveToFirst()) {
            do {
                itemModelArrayList.add(new ItemModel(
                        cursorItems.getString(1),
                        cursorItems.getString(2),
                        cursorItems.getString(3),
                        cursorItems.getInt(4),
                        cursorItems.getString(5)
                    )
                );
            } while (cursorItems.moveToNext());
        }
        cursorItems.close();
        return itemModelArrayList;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1 + TABLE_NAME_2);
        onCreate(db);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    public void moveExpiredItemsToWaste() {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_NAME_1;
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                int itemId = cursor.getInt(cursor.getColumnIndexOrThrow(ITEM_ID));
                String itemName = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_NAME));
                String expiryDateWaste = cursor.getString(cursor.getColumnIndexOrThrow(ITEM_EXPIRY));

                // Parse expiry date using LocalDate
                LocalDate expiryDate = LocalDate.parse(expiryDateWaste, DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate currentDate = LocalDate.now();

                if (!currentDate.isBefore(expiryDate)) { // equivalent to current >= expiry
                    // Move to Waste table
                    ContentValues wasteValues = new ContentValues();
                    wasteValues.put(ITEM_ID_FOREIGN, itemId);
                    wasteValues.put(ITEM_NAME_WASTE, itemName);
                    wasteValues.put(ITEM_EXPIRY_WASTE, expiryDateWaste);

                    db.insert(TABLE_NAME_2, null, wasteValues);

                    // Remove from Items table
                    //db.delete(TABLE_NAME_1, ITEM_ID + "=?", new String[]{String.valueOf(itemId)});
                }

            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

}
