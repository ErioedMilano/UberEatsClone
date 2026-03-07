package com.ubereats.dao;

import com.ubereats.model.Restaurant;
import com.ubereats.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantDAO {

    public List<Restaurant> findAll() {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT id, name, image_url, cuisine, rating FROM restaurants";

        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Restaurant r = new Restaurant();
                r.setId(rs.getInt("id"));
                r.setName(rs.getString("name"));
                r.setImageUrl(rs.getString("image_url"));
                r.setCuisine(rs.getString("cuisine"));
                r.setRating(rs.getDouble("rating"));
                restaurants.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }
    public boolean save(Restaurant restaurant) {
        String sql = "INSERT INTO restaurants (name, image_url, cuisine, rating) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getImageUrl());
            stmt.setString(3, restaurant.getCuisine());
            stmt.setDouble(4, restaurant.getRating());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Restaurant restaurant) {
        String sql = "UPDATE restaurants SET name=?, image_url=?, cuisine=?, rating=? WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, restaurant.getName());
            stmt.setString(2, restaurant.getImageUrl());
            stmt.setString(3, restaurant.getCuisine());
            stmt.setDouble(4, restaurant.getRating());
            stmt.setInt(5, restaurant.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM restaurants WHERE id=?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}