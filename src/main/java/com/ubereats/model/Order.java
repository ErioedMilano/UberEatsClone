package com.ubereats.model;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private String customerName;
    private String customerAddress;
    private LocalDateTime orderDate;
    private double total;

    public Order() {}

    public Order(int id, String customerName, String customerAddress, LocalDateTime orderDate, double total) {
        this.id = id;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.orderDate = orderDate;
        this.total = total;
    }

    // Getters en setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerAddress() { return customerAddress; }
    public void setCustomerAddress(String customerAddress) { this.customerAddress = customerAddress; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
}