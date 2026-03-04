package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.MenuItemDAO;
import com.ubereats.dao.RestaurantDAO;
import com.ubereats.model.MenuItem;
import com.ubereats.model.Restaurant;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RestaurantsHandler implements HttpHandler {

    private RestaurantDAO restaurantDAO = new RestaurantDAO();
    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // CORS
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Strip leading "/api/restaurants"
        String remaining = path.substring("/api/restaurants".length());
        if (remaining.isEmpty() || remaining.equals("/")) {
            // GET /api/restaurants
            if ("GET".equalsIgnoreCase(method)) {
                List<Restaurant> restaurants = restaurantDAO.findAll();
                String response = JsonUtils.toJson(restaurants);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
        } else if (remaining.matches("/\\d+/menu")) {
            // GET /api/restaurants/{id}/menu
            if ("GET".equalsIgnoreCase(method)) {
                String[] parts = remaining.split("/");
                int restaurantId = Integer.parseInt(parts[1]); // parts: ["", "123", "menu"]
                List<MenuItem> menuItems = menuItemDAO.findByRestaurantId(restaurantId);
                String response = JsonUtils.toJson(menuItems);
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
        }

        // Als geen match, 404
        String error = "{\"error\":\"Not found\"}";
        exchange.sendResponseHeaders(404, error.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(error.getBytes());
        os.close();
    }
}