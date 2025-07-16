package com.example.mobileproject;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddItem extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private ExecutorService cameraExecutor;
    private BarcodeScanner barcodeScanner;
    private boolean scanning = true;
    private OkHttpClient httpClient = new OkHttpClient();
    private BarcodeHandler barcodeHandler;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_item);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        dbHandler = new DBHandler(this);

        EditText nameEditText = findViewById(R.id.idEdtItemName);
        EditText barcodeEditText = findViewById(R.id.idEdtBarcode);
        EditText categoryEditText = findViewById(R.id.idEdtCategory);
        EditText quantityEditText = findViewById(R.id.idEdtQuantity);
        EditText expiryEditText = findViewById(R.id.idExpiryDate);
        Button addButton = findViewById(R.id.idBtnAddItem);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.additem);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.wastelog) {
                startActivity(new Intent(this, WasteLogActivity.class));
                return true;
            } else if (id == R.id.additem) {
                // Already in AddItem
                return true;
            } else if (id == R.id.recepie) {
                startActivity(new Intent(this, RecepieActivity.class));
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });

        // Set up the expiry date EditText with a DatePickerDialog
        EditText expiryDateEditText = findViewById(R.id.idExpiryDate);
        expiryDateEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog datePicker = new DatePickerDialog(this, (view, y, m, d) -> {
                expiryDateEditText.setText(String.format("%04d-%02d-%02d", y, m + 1, d));
            }, year, month, day);
            datePicker.show();
        });


        // Camera permission check
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }

        cameraExecutor = Executors.newSingleThreadExecutor();
        barcodeScanner = BarcodeScanning.getClient();
        barcodeHandler = new BarcodeHandler();


        addButton.setOnClickListener(v -> {
            String name = nameEditText.getText().toString().trim();
            String barcode = barcodeEditText.getText().toString().trim();
            String category = categoryEditText.getText().toString().trim();
            String quantityStr = quantityEditText.getText().toString().trim();
            String expiry = expiryEditText.getText().toString().trim();

            if (name.isEmpty() || barcode.isEmpty() || category.isEmpty() || quantityStr.isEmpty() || expiry.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            int quantity;
            try {
                quantity = Integer.parseInt(quantityStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Quantity must be a number", Toast.LENGTH_SHORT).show();
                return;
            }

            dbHandler.addNewItem(name, barcode, category, quantity, expiry);
            Toast.makeText(this, "Item added!", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

            // Optionally clear fields or finish activity
            nameEditText.setText("");
            barcodeEditText.setText("");
            categoryEditText.setText("");
            quantityEditText.setText("");
            expiryEditText.setText("");
        });
    }


    @OptIn(markerClass = ExperimentalGetImage.class)
    private void startCamera() {
        PreviewView previewView = findViewById(R.id.previewView);
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder().build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::processImageProxy);
                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalysis);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }
    @ExperimentalGetImage
    private void processImageProxy(ImageProxy imageProxy) {
        if (!scanning) {
            imageProxy.close();
            return;
        }
        android.media.Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            barcodeScanner.process(image)
                .addOnSuccessListener(barcodes -> handleBarcodes(barcodes, imageProxy))
                .addOnFailureListener(e -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private void handleBarcodes(List<Barcode> barcodes, ImageProxy imageProxy) {
        if (!barcodes.isEmpty()) {
            scanning = false;
            Barcode barcode = barcodes.get(0);
            String barcodeValue = barcode.getRawValue();
            runOnUiThread(() -> {
                TextView textView = findViewById(R.id.textView);
                textView.setText("Barcode: " + barcodeValue + "\nFetching product info...");
            });
            barcodeHandler.fetchProductInfo(barcodeValue, new BarcodeHandler.ProductInfoCallback() {
                @Override
                public void onProductInfo(String barcode, String name, String brand, String categories) {
                    TextView textView = findViewById(R.id.textView);
                    EditText nameEditText = findViewById(R.id.idEdtItemName);
                    EditText barcodeEditText = findViewById(R.id.idEdtBarcode);
                    EditText categoryEditText = findViewById(R.id.idEdtCategory);
                    nameEditText.setText(name);
                    barcodeEditText.setText(barcode);
                    categoryEditText.setText(categories);
                    textView.setText("Barcode: " + barcode + "\nProduct: " + name + "\nBrand: " + brand + "\nCategories: " + categories);
                }
                @Override
                public void onProductNotFound(String barcode) {
                    TextView textView = findViewById(R.id.textView);
                    textView.setText("Barcode: " + barcode + "\nProduct not found.");
                }
                @Override
                public void onError(String barcode, String message) {
                    TextView textView = findViewById(R.id.textView);
                    textView.setText("Barcode: " + barcode + "\nProduct lookup failed.");
                }
            });
        }
        imageProxy.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraExecutor != null) {
            cameraExecutor.shutdown();
        }
    }
}