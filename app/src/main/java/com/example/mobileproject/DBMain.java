package com.example.mobileproject;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;

public class DBMain extends AppCompatActivity {
    private EditText itemNameEdt, itemBarcodeEdt, itemCategoryEdt, itemQuantityEdt, itemExpiryEdt;
    private Button addItemBtn, readItemBtn;

    private DBHandler dbHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testing_db);
        itemNameEdt = findViewById(R.id.idEdtItemName);
        itemBarcodeEdt = findViewById(R.id.idEdtBarcode);
        itemCategoryEdt = findViewById(R.id.idEdtCategory);
        itemQuantityEdt = findViewById(R.id.idEdtQuantity);
        itemExpiryEdt = findViewById(R.id.idExpiryDate);
        addItemBtn = findViewById(R.id.idBtnAddItem);
        readItemBtn = findViewById(R.id.idBtnReadItem);
        dbHandler = new DBHandler(DBMain.this);
        addItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = itemNameEdt.getText().toString();
                String itemBarcode = itemBarcodeEdt.getText().toString();
                String itemCategory = itemCategoryEdt.getText().toString();
                String itemQuantityStr = itemQuantityEdt.getText().toString();
                Integer itemQuantity = Integer.parseInt(itemQuantityStr);
                String itemExpiry = itemExpiryEdt.getText().toString();
                if (itemName.isEmpty() && itemBarcode.isEmpty() && itemCategory.isEmpty() && itemQuantityStr.isEmpty() && itemExpiry.isEmpty()) {
                    Toast.makeText(DBMain.this, "Please enter all the data..", Toast.LENGTH_SHORT).show();
                    return;
                }
                dbHandler.addNewItem(itemName, itemBarcode, itemCategory, itemQuantity, itemExpiry);

                Toast.makeText(DBMain.this, "Course has been added.", Toast.LENGTH_SHORT).show();
                itemNameEdt.setText("");
                itemCategoryEdt.setText("");
                itemBarcodeEdt.setText("");
                itemQuantityEdt.setText("");
            }
        });

        readItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // opening a new activity via a intent.
                Intent i = new Intent(DBMain.this, ViewDatabase.class);
                startActivity(i);
            }
        });
    }
}

