package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.MenuItemDAO;
import com.ubereats.model.MenuItem;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminMenuController implements HttpHandler {

    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // GEEN CORS-HEADERS – filter doet dat

        Pattern getByRestaurantPattern = Pattern.compile("/api/admin/restaurants/(\\d+)/menu");
        Pattern singleItemPattern = Pattern.compile("/api/admin/menu/(\\d+)");
        Matcher getByRestaurantMatcher = getByRestaurantPattern.matcher(path);
        Matcher singleItemMatcher = singleItemPattern.matcher(path);

        if ("GET".equalsIgnoreCase(method) && getByRestaurantMatcher.matches()) {
            int restaurantId = Integer.parseInt(getByRestaurantMatcher.group(1));
            List<MenuItem> items = menuItemDAO.findByRestaurantId(restaurantId);
            sendResponse(exchange, 200, JsonUtils.toJson(items));
        } else if ("POST".equalsIgnoreCase(method) && path.equals("/api/admin/menu")) {
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            MenuItem item = JsonUtils.fromJson(body, MenuItem.class);
            boolean success = menuItemDAO.save(item);
            if (success) {
                sendResponse(exchange, 201, "{\"message\":\"Menu-item toegevoegd\"}");
            } else {
                sendResponse(exchange, 500, "{\"error\":\"Kon menu-item niet toevoegen\"}");
            }
        } else if ("PUT".equalsIgnoreCase(method) && singleItemMatcher.matches()) {
            int id = Integer.parseInt(singleItemMatcher.group(1));
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            MenuItem item = JsonUtils.fromJson(body, MenuItem.class);
            item.setId(id);
            boolean success = menuItemDAO.update(item);
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Menu-item bijgewerkt\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Menu-item niet gevonden\"}");
            }
        } else if ("DELETE".equalsIgnoreCase(method) && singleItemMatcher.matches()) {
            int id = Integer.parseInt(singleItemMatcher.group(1));
            boolean success = menuItemDAO.delete(id);
            if (success) {
                sendResponse(exchange, 200, "{\"message\":\"Menu-item verwijderd\"}");
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Menu-item niet gevonden\"}");
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