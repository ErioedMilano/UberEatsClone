package com.ubereats.dao;

import com.ubereats.model.Admin;
import com.ubereats.util.DatabaseUtil;
import com.ubereats.util.DatabaseUtil;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class AdminDAO {

    /*
    public static void main(String[] args) {
        AdminDAO dao = new AdminDAO();
        // Maak admin met gebruikersnaam "admin" en wachtwoord "admin123"
        boolean created = dao.createAdmin("admin", "admin123");
        if (created) {
            System.out.println("Admin aangemaakt!");
        } else {
            System.out.println("Admin bestond mogelijk al of er trad een fout op.");
        }
    }
     */

    public Admin findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, created_at FROM admins WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Admin admin = new Admin();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPasswordHash(rs.getString("password_hash"));
                admin.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return admin;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validatePassword(String plainPassword, String hashed) {
        return BCrypt.checkpw(plainPassword, hashed);
    }

    // Optioneel: methode om nieuwe admin toe te voegen (voor setup)
    public boolean createAdmin(String username, String plainPassword) {
        String hash = BCrypt.hashpw(plainPassword, BCrypt.gensalt());
        String sql = "INSERT INTO admins (username, password_hash) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, hash);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}