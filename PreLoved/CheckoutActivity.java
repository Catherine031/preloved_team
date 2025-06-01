package com.example.preloved;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import java.text.DecimalFormat;

import java.util.ArrayList;

public class CheckoutActivity extends AppCompatActivity {

    TextView totalPriceText;
    Button logoutButton;
    double total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        totalPriceText = findViewById(R.id.total_price_text);
        logoutButton = findViewById(R.id.logout_button);

        ArrayList<CartItem> cartItems = (ArrayList<CartItem>) getIntent().getSerializableExtra("cartItems");

        if (cartItems != null) {
            for (CartItem item : cartItems) {
                total += Double.parseDouble(item.getPrice()) * item.getQuantity();

            }
        }

        DecimalFormat df = new DecimalFormat("#.00");
        totalPriceText.setText("Total: R" + df.format(total));



        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to MainActivity
                Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}

