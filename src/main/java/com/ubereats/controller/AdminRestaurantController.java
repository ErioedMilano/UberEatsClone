package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.RestaurantDAO;
import com.ubereats.model.Restaurant;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class AdminRestaurantController implements HttpHandler {

    private RestaurantDAO restaurantDAO = new RestaurantDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // GEEN CORS-HEADERS HIER – de filter doet dat al

        // GET alle restaurants
        if ("GET".equalsIgnoreCase(method) && path.equals("/api/admin/restaurants")) {
            List<Restaurant> restaurants = restaurantDAO.findAll();
            String response = JsonUtils.toJson(restaurants);
            sendResponse(exchange, 200, response);
        }
        // POST nieuw restaurant
        else if ("POST".equalsIgnoreCase(method) && path.equals("/api/admin/restaurants")) {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Restaurant newRestaurant = JsonUtils.fromJson(body, Restaurant.class);
            boolean success = restaurantDAO.save(newRestaurant);
            if (success) {
                sendResponse(exchange, 201, "{\"message\":\"Restaurant toegevoegd\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Kon restaurant niet toevoegen\"}");
            }
        }
        // PUT bestaand restaurant (bijv. /api/admin/restaurants/5)
        else if ("PUT".equalsIgnoreCase(method) && path.matches("/api/admin/restaurants/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            Restaurant restaurant = JsonUtils.fromJson(body, Restaurant.class);
            restaurant.setId(id);
            boolean success = restaurantDAO.update(restaurant);
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Restaurant bijgewerkt\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Restaurant niet gevonden\"}");
            }
        }
        // DELETE restaurant (bijv. /api/admin/restaurants/5)
        else if ("DELETE".equalsIgnoreCase(method) && path.matches("/api/admin/restaurants/\\d+")) {
            String[] parts = path.split("/");
            int id = Integer.parseInt(parts[parts.length - 1]);
            boolean success = restaurantDAO.delete(id);
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Restaurant verwijderd\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Restaurant niet gevonden\"}");
            }
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}