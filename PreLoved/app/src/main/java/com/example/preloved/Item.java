package com.example.preloved;

public class Item {
    private String title;
    private String price;
    private String username;
    private String imageUrl;

    public Item(String title, String price, String username, String imageUrl) {
        this.title = title;
        this.price = price;
        this.username = username;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getUsername() {
        return username;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
