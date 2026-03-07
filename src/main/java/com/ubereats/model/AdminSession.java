package com.ubereats.model;

import java.time.LocalDateTime;

public class AdminSession {
    private int id;
    private int adminId;
    private String token;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    // Default constructor
    public AdminSession() {
    }

    // Constructor zonder id (voor nieuwe sessie)
    public AdminSession(int adminId, String token, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.adminId = adminId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    // Constructor met ALLE velden
    public AdminSession(int id, int adminId, String token, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.adminId = adminId;
        this.token = token;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getAdminId() {
        return adminId;
    }

    public String getToken() {
        return token;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}