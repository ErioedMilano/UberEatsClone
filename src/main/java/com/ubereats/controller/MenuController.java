package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.MenuItemDAO;
import com.ubereats.model.MenuItem;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuController implements HttpHandler {

    private MenuItemDAO menuItemDAO = new MenuItemDAO();

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

        if ("GET".equalsIgnoreCase(method)) {
            // Pad patroon: /api/restaurants/{id}/menu
            Pattern pattern = Pattern.compile("/api/restaurants/(\\d+)/menu");
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches()) {
                int restaurantId = Integer.parseInt(matcher.group(1));
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

        // Als we hier komen: niet gevonden
        String error = "{\"error\":\"Not found\"}";
        exchange.sendResponseHeaders(404, error.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(error.getBytes());
        os.close();
    }
}