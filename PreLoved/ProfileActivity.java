package com.example.preloved;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
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

public class ProfileActivity extends AppCompatActivity {
    ImageView profileImageView;
    TextView textUsername, textEmail;
    RecyclerView recyclerMyItems;
    ItemAdapter itemAdapter;
    List<Item> itemList = new ArrayList<>();
    int userId;
    String username;

    private RecyclerView recyclerView;
    private EditText searchBar;
    private ItemAdapter adapter;
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 4;


    private String baseUrl = "http://10.0.2.2/preloved/fetch_items.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImageView = findViewById(R.id.profileImageView);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        recyclerMyItems = findViewById(R.id.recyclerMyItems);
        ImageButton btn_home = findViewById(R.id.go_home);
        ImageButton btn_cart = findViewById(R.id.cart);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        int userId = prefs.getInt("user_id", -1);
        String username = prefs.getString("username", "Unknown");



        textUsername.setText(username);

        recyclerMyItems.setLayoutManager(new GridLayoutManager(this, 2));

        recyclerMyItems.setAdapter(itemAdapter);

        // 🔧 Initialize adapter with context and list
        itemAdapter = new ItemAdapter(this, itemList, userId);

        // 🔧 Set layout manager and adapter
        recyclerMyItems.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerMyItems.setAdapter(itemAdapter);

        // Fetch data
        fetchUserProfile(userId);
        fetchUserItems(userId);

        fetchUserProfile(userId);
        fetchUserItems(userId);

        btn_home.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();


        });

        btn_cart.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish();


        });
    }

    private void fetchUserProfile(int userId) {
        String url = "http://10.0.2.2/preloved/get_user_profile.php?user_id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONObject obj = new JSONObject(response);
                textEmail.setText(obj.getString("email"));
                // Load profile image if needed
            } catch (Exception e) {
                Toast.makeText(this, "Profile error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "Error loading profile", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }

    private void fetchUserItems(int userId) {
        String url = "http://10.0.2.2/preloved/fetch_user_items.php?user_id=" + userId;
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                itemList.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    itemList.add(new Item(
                            obj.getString("title"),
                            obj.getString("price"),
                            obj.getString("username"), // ✅ Add username just like in HomeActivity
                            obj.getString("item_image")
                    ));
                }
                itemAdapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }, error -> {
            Toast.makeText(this, "Item load error", Toast.LENGTH_SHORT).show();
        });

        queue.add(request);
    }
}