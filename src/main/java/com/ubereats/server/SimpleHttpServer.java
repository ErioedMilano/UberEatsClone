package com.ubereats.server;

import com.sun.net.httpserver.HttpServer;
import com.ubereats.controller.*;
import com.ubereats.filter.AdminAuthFilter;

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // health check endpoint te maken
        server.createContext("/", exchange -> {
            String response = "OK";
            exchange.sendResponseHeaders(200, response.getBytes().length);
            exchange.getResponseBody().write(response.getBytes());
            exchange.getResponseBody().close();
        });

        // Publieke endpoints
        server.createContext("/api/restaurants", new RestaurantController());
        server.createContext("/api/restaurants/", new MenuController());
        server.createContext("/api/orders", new OrderController());

        // Admin endpoints (geen auth)
        server.createContext("/api/admin/login", new AdminAuthController());
        server.createContext("/api/admin/logout", new AdminAuthController());

        // Beveiligde admin endpoints
        server.createContext("/api/admin/restaurants", new AdminAuthFilter(new AdminRestaurantController()));
        server.createContext("/api/admin/menu", new AdminAuthFilter(new AdminMenuController()));
        server.createContext("/api/admin/orders", new AdminAuthFilter(new AdminOrderController()));
        server.createContext("/api/admin/dashboard", new AdminAuthFilter(new AdminDashboardController()));

        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port " + port);
    }
}