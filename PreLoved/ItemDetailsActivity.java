package com.example.preloved;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ItemDetailsActivity extends AppCompatActivity {

    private ImageView itemImage;
    private TextView itemTitle, itemDescription, itemPrice, itemUsername, averageRatingText;
    private RatingBar ratingBar;
    private TextView ratingCountText;
    private TextView textQuantity;


    private String currentTitle = null;

    private int itemId = -1;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        // Initialize class field userId
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);

        currentTitle = getIntent().getStringExtra("title");

        // Now initialize views (textQuantity was redeclared locally before - fix that)
        textQuantity = findViewById(R.id.text_quantity);

        // Call fetchCartQuantity AFTER currentTitle and userId are set
        if (userId != -1 && currentTitle != null) {
            fetchCartQuantity(userId, currentTitle);
            loadItemDetails(currentTitle);
        }

        ImageButton btn_home = findViewById(R.id.go_home);


        itemImage = findViewById(R.id.item_image);
        itemTitle = findViewById(R.id.item_title);
        itemDescription = findViewById(R.id.item_description);
        itemPrice = findViewById(R.id.item_price);
        itemUsername = findViewById(R.id.item_username);
        averageRatingText = findViewById(R.id.average_rating);
        ratingBar = findViewById(R.id.rating_bar);
        Button submitRatingButton = findViewById(R.id.submit_rating);
        String title = getIntent().getStringExtra("title");

        int userId = prefs.getInt("user_id", -1);

        ratingCountText = findViewById(R.id.rating_count);
        TextView textQuantity = findViewById(R.id.text_quantity);

        fetchCartQuantity(userId, currentTitle);



        currentTitle = getIntent().getStringExtra("title");
        if (currentTitle != null) {
            loadItemDetails(currentTitle);
        }



        submitRatingButton.setOnClickListener(v -> {
            float ratingValue = ratingBar.getRating();
            Toast.makeText(this, "itemId: " + itemId + ", userId: " + userId, Toast.LENGTH_LONG).show();

            if (itemId != -1 && userId != -1) {
                submitRating(itemId, userId, ratingValue);
            } else {
                Toast.makeText(this, "Unable to submit rating: Missing itemId or userId", Toast.LENGTH_LONG).show();
            }
        });

        Button btnAddToCart = findViewById(R.id.btn_add_to_cart);
        Button btnRemoveFromCart = findViewById(R.id.btn_remove_from_cart);

        btnAddToCart.setOnClickListener(v -> {
            modifyCart("add", currentTitle, userId);
        });

        btnRemoveFromCart.setOnClickListener(v -> {
            modifyCart("remove", currentTitle, userId);
        });


        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(ItemDetailsActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });


    }

    private void loadItemDetails(String title) {
        String url = "http://10.0.2.2/preloved/get_item_details.php?title=" + title + "&user_id=" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            JSONObject item = json.getJSONObject("item");

                            // Extract item fields
                            itemId = item.getInt("item_id");
                            String titleText = item.getString("title");
                            String description = item.getString("description");
                            String price = item.getString("price");
                            String username = item.getString("username");
                            String imageUrl = item.getString("item_image");

                            double averageRating = item.optDouble("average_rating", 0.0);
                            int ratingCount = item.optInt("rating_count", 0);

                            // Set UI content
                            itemTitle.setText(titleText);
                            itemDescription.setText(description);
                            itemPrice.setText("R " + price);
                            itemUsername.setText(username);

                            /// Set UI content
                            averageRatingText.setText("Average Rating: " + String.format("%.1f", averageRating));
                            ratingCountText.setText("Ratings: " + ratingCount);

                            // Allow user to rate regardless of past rating
                            ratingBar.setRating(0);
                            ratingBar.setIsIndicator(false);

                            // Load image
                            Glide.with(this).load(imageUrl).into(itemImage);

                        } else {
                            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error parsing item data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error loading item", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }


    private void submitRating(int itemId, int userId, float ratingValue) {
        String url = "http://10.0.2.2/preloved/submit_rating.php";

        StringRequest request = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String message = json.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        // Clear rating bar and refresh item details
                        ratingBar.setRating(0);
                        if (currentTitle != null) {
                            loadItemDetails(currentTitle);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(this, "Error parsing response: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Volley error: " + error.toString(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("item_id", String.valueOf(itemId));
                params.put("user_id", String.valueOf(userId));
                params.put("rating_value", String.valueOf(ratingValue));
                params.put("title", currentTitle);  // ✅ THIS IS WHAT WAS MISSING!
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    private void modifyCart(String action, String itemTitle, int userId) {
        String url = "http://10.0.2.2/preloved/cart_add_item.php";  // single endpoint handles add/remove
        int quantityChange = action.equals("add") ? 1 : -1;

        StringRequest request = new StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        String message = json.getString("message");
                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        // After success, fetch the latest quantity and update UI
                        fetchCartQuantity(userId, itemTitle);
                    } catch (JSONException e) {
                        Toast.makeText(this, "Response error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Network error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", String.valueOf(userId));
                params.put("item_title", itemTitle);
                params.put("quantity", String.valueOf(quantityChange));  // +1 or -1
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }


    private void fetchCartQuantity(int userId, String itemTitle) {
        String url = "http://10.0.2.2/preloved/fetch_cart_details.php?user_id=" + userId;

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject item = response.getJSONObject(i);
                            if (item.getString("item_title").equals(itemTitle)) {
                                int quantity = item.getInt("quantity");
                                textQuantity.setText("Quantity: " + quantity);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Log.e("FETCH_CART_ERROR", error.toString())
        );

        queue.add(request);
    }




}