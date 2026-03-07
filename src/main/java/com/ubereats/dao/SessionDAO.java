package com.ubereats.dao;

import com.ubereats.model.AdminSession;
import com.ubereats.util.DatabaseUtil;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class SessionDAO {

    public String createSession(int adminId) {
        String token = UUID.randomUUID().toString().replace("-", "");
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24); // 24 uur geldig

        String sql = "INSERT INTO admin_sessions (admin_id, token, expires_at) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            stmt.setString(2, token);
            stmt.setTimestamp(3, Timestamp.valueOf(expiresAt));
            stmt.executeUpdate();
            return token;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public AdminSession validateToken(String token) {
        String sql = "SELECT id, admin_id, token, expires_at, created_at FROM admin_sessions WHERE token = ? AND expires_at > NOW()";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                AdminSession session = new AdminSession();
                session.setId(rs.getInt("id"));
                session.setAdminId(rs.getInt("admin_id"));
                session.setToken(rs.getString("token"));
                session.setExpiresAt(rs.getTimestamp("expires_at").toLocalDateTime());
                session.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return session;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteSession(String token) {
        String sql = "DELETE FROM admin_sessions WHERE token = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, token);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}