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
}