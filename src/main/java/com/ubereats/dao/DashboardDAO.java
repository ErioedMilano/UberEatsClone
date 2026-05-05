package com.ubereats.dao;

import com.ubereats.model.RestaurantStat;
import com.ubereats.model.MenuItemStat;
import com.ubereats.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardDAO {

    public List<RestaurantStat> getTopRestaurants(int limit) {
        List<RestaurantStat> list = new ArrayList<>();
        String sql = "SELECT r.id, r.name, COUNT(DISTINCT o.id) as order_count " +
                "FROM restaurants r " +
                "LEFT JOIN menu_items mi ON r.id = mi.restaurant_id " +
                "LEFT JOIN order_items oi ON mi.id = oi.menu_item_id " +
                "LEFT JOIN orders o ON oi.order_id = o.id " +
                "GROUP BY r.id, r.name " +
                "ORDER BY order_count DESC LIMIT ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(new RestaurantStat(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("order_count"),
                        0 // totalItems (optioneel)
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MenuItemStat> getTopDishes(int limit) {
        List<MenuItemStat> list = new ArrayList<>();
        String sql = "SELECT mi.id, mi.name, SUM(oi.quantity) as total_quantity " +
                "FROM menu_items mi " +
                "LEFT JOIN order_items oi ON mi.id = oi.menu_item_id " +
                "GROUP BY mi.id, mi.name " +
                "ORDER BY total_quantity DESC LIMIT ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int totalQty = rs.getInt("total_quantity");
                list.add(new MenuItemStat(
                        rs.getInt("id"),
                        rs.getString("name"),
                        0.0, // price (niet in query)
                        0,   // orderCount (niet in query)
                        totalQty
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalOrders() {
        String sql = "SELECT COUNT(*) as total FROM orders";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalOrdersToday() {
        String sql = "SELECT COUNT(*) as total FROM orders WHERE DATE(order_date) = CURDATE()";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public double getRevenueToday() {
        String sql = "SELECT COALESCE(SUM(total), 0) as revenue FROM orders WHERE DATE(order_date) = CURDATE()";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble("revenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, Integer> getOrdersByLast24Hours() {
        Map<String, Integer> map = new LinkedHashMap<>();
        String sql = "SELECT HOUR(order_date) as hour, COUNT(*) as count " +
                "FROM orders " +
                "WHERE order_date >= NOW() - INTERVAL 24 HOUR " +
                "GROUP BY HOUR(order_date) " +
                "ORDER BY hour";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(String.format("%02d:00", rs.getInt("hour")), rs.getInt("count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }
}
