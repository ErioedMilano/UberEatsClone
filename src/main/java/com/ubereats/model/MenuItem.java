package com.ubereats.model;

public class MenuItem {
    private int id;
    private int restaurantId;
    private String name;
    private String description;
    private double price;
    private String imageUrl;  // nieuw

    public MenuItem() {}

    public MenuItem(int id, int restaurantId, String name, String description, double price, String imageUrl) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // Getters en setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getRestaurantId() { return restaurantId; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}