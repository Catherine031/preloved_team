package com.example.preloved;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddItemsActivity extends AppCompatActivity {

    private EditText titleEditText, descriptionEditText, priceEditText, imageUrlEditText;
    private Button submitButton;
    private int userId; // from SharedPreferences or Intent

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item);

        titleEditText = findViewById(R.id.titleEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        imageUrlEditText = findViewById(R.id.imageUrlEditText);
        submitButton = findViewById(R.id.submitButton);

        // Get user ID from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = sharedPreferences.getInt("user_id", -1); // -1 if not found

        submitButton.setOnClickListener(v -> {
            String title = titleEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            String priceStr = priceEditText.getText().toString().trim();
            String imageUrl = imageUrlEditText.getText().toString().trim();

            if (title.isEmpty() || priceStr.isEmpty()) {
                Toast.makeText(this, "Title and price are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price;
            try {
                price = Double.parseDouble(priceStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                return;
            }

            addItemToDatabase(userId, title, description, price, imageUrl);
        });
    }

    private void addItemToDatabase(int userId, String title, String description, double price, String imageUrl) {
        String url = "http://10.0.2.2/preloved/add_item.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(this, "Item added successfully", Toast.LENGTH_SHORT).show();
                    finish(); // go back to previous screen
                },
                error -> {
                    String errorMsg = "Error: ";
                    if (error.networkResponse != null && error.networkResponse.data != null) {
                        errorMsg += new String(error.networkResponse.data);
                    } else {
                        errorMsg += error.toString();
                    }
                    Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("VolleyError", errorMsg);
                }
        )

        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("title", title);
                params.put("description", description);
                params.put("price", String.valueOf(price));
                params.put("item_image", imageUrl); // optional
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}