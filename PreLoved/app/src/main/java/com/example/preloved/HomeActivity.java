package com.example.preloved;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.ImageButton;
import android.widget.Toast;


public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private EditText searchBar;
    private ItemAdapter adapter;
    private List<Item> itemList = new ArrayList<>();
    private int currentPage = 0;
    private static final int ITEMS_PER_PAGE = 4;
    private String baseUrl = "http://10.0.2.2/preloved/fetch_items.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ImageButton btnProfile = findViewById(R.id.btn_profile);
        ImageButton addItemButton = findViewById(R.id.addItemButton);

        int userId = getIntent().getIntExtra("user_id", -1);


        recyclerView = findViewById(R.id.item_recycler);
        searchBar = findViewById(R.id.search_bar);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(this, itemList, userId);
        recyclerView.setAdapter(adapter);



        String username = getIntent().getStringExtra("username");

        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("user_id", userId);
            intent.putExtra("username", username);
            startActivity(intent);
        });

        addItemButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AddItemsActivity.class);
            startActivity(intent);
        });



        loadItems("");




        searchBar.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentPage = 0;
                loadItems(s.toString());
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadItems(String query) {
        String url = baseUrl + "?page=" + currentPage + "&query=" + Uri.encode(query);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        if (currentPage == 0) itemList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            Item item = new Item(
                                    obj.getString("title"),
                                    obj.getString("price"),
                                    obj.getString("username"),
                                    obj.getString("item_image")
                                    );
                            itemList.add(item);
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(HomeActivity.this, "Error loading items", Toast.LENGTH_SHORT).show()
        );
        Volley.newRequestQueue(this).add(stringRequest);
    }





}
