package com.ubereats.model;

import java.util.List;
import java.util.Map;

public class DashboardResponse {
    private List<RestaurantStat> topRestaurants;
    private List<MenuItemStat> topDishes;
    private int totalOrdersToday;
    private double revenueToday;
    private Map<String, Integer> ordersByHour;

    public DashboardResponse() {}

    public DashboardResponse(List<RestaurantStat> topRestaurants, List<MenuItemStat> topDishes, int totalOrdersToday, double revenueToday, Map<String, Integer> ordersByHour) {
        this.topRestaurants = topRestaurants;
        this.topDishes = topDishes;
        this.totalOrdersToday = totalOrdersToday;
        this.revenueToday = revenueToday;
        this.ordersByHour = ordersByHour;
    }

    // Getters en setters
    public List<RestaurantStat> getTopRestaurants() { return topRestaurants; }
    public void setTopRestaurants(List<RestaurantStat> topRestaurants) { this.topRestaurants = topRestaurants; }

    public List<MenuItemStat> getTopDishes() { return topDishes; }
    public void setTopDishes(List<MenuItemStat> topDishes) { this.topDishes = topDishes; }

    public int getTotalOrdersToday() { return totalOrdersToday; }
    public void setTotalOrdersToday(int totalOrdersToday) { this.totalOrdersToday = totalOrdersToday; }

    public double getRevenueToday() { return revenueToday; }
    public void setRevenueToday(double revenueToday) { this.revenueToday = revenueToday; }

    public Map<String, Integer> getOrdersByHour() { return ordersByHour; }
    public void setOrdersByHour(Map<String, Integer> ordersByHour) { this.ordersByHour = ordersByHour; }
}