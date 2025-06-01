package com.example.preloved;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private TextView textUsername, textEmail;
    private ImageView profileImageView;
    private RecyclerView recyclerCartItems;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);  // Ensure correct layout file

        // Initialize UI components
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        recyclerCartItems = findViewById(R.id.recyclerCartItems);
        ImageButton btn_home = findViewById(R.id.go_home);
        ImageButton checkoutButton = findViewById(R.id.checkout);




        // Get user info from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        userId = prefs.getInt("user_id", -1);
        String username = prefs.getString("username", "Unknown");

        // Display user data
        textUsername.setText(username);

        // Setup RecyclerView
        recyclerCartItems.setLayoutManager(new GridLayoutManager(this, 2));
        cartAdapter = new CartAdapter(this, cartItemList, userId);
        recyclerCartItems.setAdapter(cartAdapter);

        // Load profile & cart data
        fetchUserProfile(userId);
        fetchCartItems(userId);

        // Home button logic
        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(CartActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                intent.putExtra("cartItems", new ArrayList<>(cartItemList)); // assuming you have a cartItems list
                startActivity(intent);
            }
        });


    }

    private void fetchUserProfile(int userId) {
        String url = "http://10.0.2.2/preloved/get_user_profile.php?user_id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                textEmail.setText(obj.getString("email"));
                // You can add loading of profile image here
            } catch (Exception e) {
                Toast.makeText(this, "Profile error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }

    private void fetchCartItems(int userId) {
        String url = "http://10.0.2.2/preloved/fetch_cart_items.php?user_id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                cartItemList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    cartItemList.add(new CartItem(
                            obj.getString("item_title"),
                            obj.getString("price"),
                            obj.optString("item_image", ""),
                            obj.getInt("quantity")
                    ));
                }
                cartAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, "Cart parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "Error loading cart items", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }
}
