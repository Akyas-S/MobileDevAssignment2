package com.example.mobileproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.ai.FirebaseAI;
import com.google.firebase.ai.GenerativeModel;
import com.google.firebase.ai.java.GenerativeModelFutures;
import com.google.firebase.ai.type.Content;
import com.google.firebase.ai.type.GenerateContentResponse;
import com.google.firebase.ai.type.GenerativeBackend;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RecepieActivity extends AppCompatActivity {

    private TextView recipeText;
    private MaterialButton generateButton;
    private DBHandler dbHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_recepie);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        recipeText = findViewById(R.id.recipeText);
        generateButton = findViewById(R.id.generateButton);
        dbHandler = new DBHandler(this);

        recipeText.setText("Click 'Generate Recipe' to get a recipe suggestion based on your ingredients.");

        generateButton.setOnClickListener(v -> {
            generateButton.setEnabled(false);
            generateButton.setText("Generating...");
            generateRecipe();
        });

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setSelectedItemId(R.id.recepie);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (id == R.id.wastelog) {
                startActivity(new Intent(this, WasteLogActivity.class));
                return true;
            } else if (id == R.id.additem) {
                startActivity(new Intent(this, AddItem.class));
                return true;
            } else if (id == R.id.recepie) {
                // Already in RecepieActivity
                return true;
            } else if (id == R.id.profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            }
            return false;
        });
    }

    private void generateRecipe() {
        ArrayList<ItemModel> items = dbHandler.readItems();

        if (items.isEmpty()) {
            recipeText.setText("No ingredients found in your inventory.\n\nAdd some items first to get recipe suggestions!");
            generateButton.setEnabled(true);
            generateButton.setText("Generate Recipe");
            return;
        }

        recipeText.setText("Looking for the perfect recipe using your ingredients...\n\nThis might take a moment.");
        String ingredients = items.stream()
                .map(ItemModel::getItemName)
                .collect(Collectors.joining(", "));

        String promptText = String.format(
            "I have these ingredients: %s\n\n" +
            "Create a delicious recipe using these ingredients. " +
            "Format the response exactly like this:\n\n" +
            "🍳 [Recipe Name]\n\n" +
            "Ingredients Needed:\n" +
            "• [ingredient 1 with quantity]\n" +
            "• [ingredient 2 with quantity]\n\n" +
            "Instructions:\n" +
            "1. [First step]\n" +
            "2. [Second step]\n" +
            "[etc...]\n\n" +
            "⏱️ Cooking Time: [time]\n" +
            "👥 Serves: [number]\n\n" +
            "Keep it practical and use mostly what's available.",
            ingredients
        );

        GenerativeModel ai = FirebaseAI.getInstance(GenerativeBackend.googleAI())
                .generativeModel("gemini-2.5-flash");

        Content prompt = new Content.Builder().addText(promptText).build();
        ListenableFuture<GenerateContentResponse> future = GenerativeModelFutures.from(ai).generateContent(prompt);

        Futures.addCallback(future,
            new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse response) {
                    runOnUiThread(() -> {
                        String formattedText = response.getText()
                            .replace("•", "\n•")
                            .replace("\n\n\n", "\n\n")
                            .trim();
                        recipeText.setText(formattedText);
                        generateButton.setEnabled(true);
                        generateButton.setText("Generate New Recipe");
                    });
                }

                @Override
                public void onFailure(Throwable t) {
                    runOnUiThread(() -> {
                        recipeText.setText("⚠️ Couldn't generate recipe\n\n" +
                            "Please check your internet connection and try again.");
                        generateButton.setEnabled(true);
                        generateButton.setText("Try Again");
                    });
                }
            },
            ContextCompat.getMainExecutor(this)
        );
    }
}
