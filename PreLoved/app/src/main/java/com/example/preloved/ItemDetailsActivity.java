package com.example.preloved;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.RatingBar;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
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

    private int itemId = -1;
    private int userId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);


        itemImage = findViewById(R.id.item_image);
        itemTitle = findViewById(R.id.item_title);
        itemDescription = findViewById(R.id.item_description);
        itemPrice = findViewById(R.id.item_price);
        itemUsername = findViewById(R.id.item_username);
        averageRatingText = findViewById(R.id.average_rating);
        ratingBar = findViewById(R.id.rating_bar);
        Button submitRatingButton = findViewById(R.id.submit_rating);
        String title = getIntent().getStringExtra("title");
        userId = getIntent().getIntExtra("user_id", -1);

        if (title != null) {
            loadItemDetails(title);
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

                            double averageRating = item.optDouble("average_rating", 4.0);
                            int ratingCount = item.optInt("rating_count", 0);
                            double userRating = item.optDouble("user_rating", 0.0);

                            // Set UI content
                            itemTitle.setText(titleText);
                            itemDescription.setText(description);
                            itemPrice.setText("R " + price);
                            itemUsername.setText(username);

                            // Set average rating text
                            averageRatingText.setText("Average Rating: " + averageRating + " (" + ratingCount + " ratings)");

                            // Set user rating in RatingBar
                            if (userRating > 0) {
                                ratingBar.setRating((float) userRating);
                                ratingBar.setIsIndicator(true);  // Disable if already rated
                            } else {
                                ratingBar.setRating(0);
                                ratingBar.setIsIndicator(false); // Allow rating if not rated
                            }

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

        com.android.volley.toolbox.StringRequest request = new com.android.volley.toolbox.StringRequest(
                Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            double averageRating = json.getDouble("average_rating");
                            int totalRatings = json.getInt("total_ratings");

                            averageRatingText.setText("Average Rating: " + averageRating + " (" + totalRatings + " ratings)");
                            Toast.makeText(this, "Rating submitted! New average: " + averageRating, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
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
                return params;
            }

            // ✅ This line ensures parameters are sent in the correct format
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
}

