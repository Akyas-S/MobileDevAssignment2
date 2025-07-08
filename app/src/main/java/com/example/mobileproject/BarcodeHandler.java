package com.example.mobileproject;

import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import com.google.mlkit.vision.barcode.common.Barcode;

import org.json.JSONObject;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class BarcodeHandler {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ProductInfoCallback {
        void onProductInfo(String barcode, String name, String brand, String categories);
        void onProductNotFound(String barcode);
        void onError(String barcode, String message);
    }

    public void fetchProductInfo(String barcode, ProductInfoCallback callback) {
        String url = "https://world.openfoodfacts.org/api/v0/product/" + barcode + ".json";
        Request request = new Request.Builder().url(url).build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mainHandler.post(() -> callback.onError(barcode, "Product lookup failed."));
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    JSONObject json = new JSONObject(result);
                    if (json.optInt("status") == 1) {
                        JSONObject product = json.getJSONObject("product");
                        String name = product.optString("product_name", "N/A");
                        String brand = product.optString("brands", "N/A");
                        String categories = product.optString("categories", "N/A");
                        mainHandler.post(() -> callback.onProductInfo(barcode, name, brand, categories));
                    } else {
                        mainHandler.post(() -> callback.onProductNotFound(barcode));
                    }
                } catch (Exception e) {
                    mainHandler.post(() -> callback.onError(barcode, "Error parsing product info."));
                }
            }
        });
    }
}
