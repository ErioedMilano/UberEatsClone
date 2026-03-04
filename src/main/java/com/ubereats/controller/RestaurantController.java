package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.RestaurantDAO;
import com.ubereats.model.Restaurant;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class RestaurantController implements HttpHandler {

    private RestaurantDAO restaurantDAO = new RestaurantDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("GET".equalsIgnoreCase(method) && path.equals("/api/restaurants")) {
            List<Restaurant> restaurants = restaurantDAO.findAll();
            String response = JsonUtils.toJson(restaurants);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            String error = "{\"error\":\"Not found\"}";
            exchange.sendResponseHeaders(404, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }
}