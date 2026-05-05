package com.ubereats.model;

public class MenuItemStat {
    private int id;
    private String name;
    private double price;
    private int orderCount;
    private int totalQuantity;

    public MenuItemStat() {}

    public MenuItemStat(int id, String name, double price, int orderCount, int totalQuantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderCount = orderCount;
        this.totalQuantity = totalQuantity;
    }

    // Getters en setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getOrderCount() { return orderCount; }
    public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
    public int getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(int totalQuantity) { this.totalQuantity = totalQuantity; }
}