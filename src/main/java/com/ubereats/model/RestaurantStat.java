package com.ubereats.model;

public class RestaurantStat {
    private int id;
    private String name;
    private int orderCount;
    private int totalItems;

    public RestaurantStat(int id, String name, int orderCount) {}

    public RestaurantStat(int id, String name, int orderCount, int totalItems) {
        this.id = id;
        this.name = name;
        this.orderCount = orderCount;
        this.totalItems = totalItems;
    }

    // Getters en setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    public int getTotalItems() { return totalItems; }
    public void setTotalItems(int totalItems) { this.totalItems = totalItems; }
}