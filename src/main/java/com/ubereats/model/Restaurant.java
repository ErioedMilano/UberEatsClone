package com.ubereats.model;

public class Restaurant {
    private int id;
    private String name;
    private String imageUrl;
    private String cuisine;
    private double rating;

    public Restaurant() {}

    public Restaurant(int id, String name, String imageUrl, String cuisine, double rating) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.cuisine = cuisine;
        this.rating = rating;
    }

    // Getters en setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCuisine() { return cuisine; }
    public void setCuisine(String cuisine) { this.cuisine = cuisine; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
}