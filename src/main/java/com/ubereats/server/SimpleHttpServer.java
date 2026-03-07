package com.ubereats.server;

import com.sun.net.httpserver.HttpServer;
import com.ubereats.controller.*;
import com.ubereats.filter.AdminAuthFilter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Publieke endpoints (geen auth nodig)
        server.createContext("/api/restaurants", new RestaurantController());
        server.createContext("/api/restaurants/", new MenuController());
        server.createContext("/api/orders", new OrderController());

        // Admin endpoints (beveiligd met token)
        server.createContext("/api/admin/login", new AdminAuthController());
        server.createContext("/api/admin/logout", new AdminAuthController()); // logout via zelfde controller

        // Beveiligde admin endpoints (via filter)
        server.createContext("/api/admin/restaurants", new AdminAuthFilter(new AdminRestaurantController()));
        // Later: /api/admin/menu, /api/admin/orders, /api/admin/dashboard

        // Admin endpoints (beveiligd met token)
        server.createContext("/api/admin/login", new AdminAuthController());
        server.createContext("/api/admin/logout", new AdminAuthController());

// Beveiligde admin endpoints (via filter)
        server.createContext("/api/admin/restaurants", new AdminAuthFilter(new AdminRestaurantController()));
        server.createContext("/api/admin/menu", new AdminAuthFilter(new AdminMenuController()));
        server.createContext("/api/admin/orders", new AdminAuthFilter(new AdminOrderController()));
        server.createContext("/api/admin/dashboard", new AdminAuthFilter(new AdminDashboardController()));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}