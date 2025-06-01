package com.example.preloved;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String title;
    private String price;
    private String imageUrl;
    private int quantity; // ✅ Added

    public CartItem(String title, String price, String imageUrl, int quantity) {
        this.title = title;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }
}
