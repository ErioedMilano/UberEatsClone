package com.ubereats.dao;

import com.ubereats.model.Order;
import com.ubereats.model.OrderItem;
import com.ubereats.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;

public class OrderDAO {

    public int save(Order order, java.util.List<OrderItem> items) {
        Connection conn = null;
        PreparedStatement orderStmt = null;
        PreparedStatement itemStmt = null;
        ResultSet generatedKeys = null;
        int orderId = -1;

        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); // Begin transactie

            // 1. Insert order
            String orderSql = "INSERT INTO orders (customer_name, customer_address, order_date, total) VALUES (?, ?, ?, ?)";
            orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setString(1, order.getCustomerName());
            orderStmt.setString(2, order.getCustomerAddress());
            orderStmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            orderStmt.setDouble(4, order.getTotal());
            orderStmt.executeUpdate();

            generatedKeys = orderStmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                orderId = generatedKeys.getInt(1);
            } else {
                throw new SQLException("Aanmaken order mislukt, geen ID verkregen.");
            }

            // 2. Insert order items
            String itemSql = "INSERT INTO order_items (order_id, menu_item_id, quantity, price) VALUES (?, ?, ?, ?)";
            itemStmt = conn.prepareStatement(itemSql);
            for (OrderItem item : items) {
                itemStmt.setInt(1, orderId);
                itemStmt.setInt(2, item.getMenuItemId());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.addBatch();
            }
            itemStmt.executeBatch();

            conn.commit(); // Alles gelukt
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            e.printStackTrace();
        } finally {
            // Sluit resources
            try { if (generatedKeys != null) generatedKeys.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (orderStmt != null) orderStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (itemStmt != null) itemStmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
        return orderId;
    }
}