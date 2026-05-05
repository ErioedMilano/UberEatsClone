package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.MenuItemDAO;
import com.ubereats.model.MenuItem;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminMenuController implements HttpHandler {

    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    private Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null) return map;
        for (String param : query.split("&")) {
            String[] pair = param.split("=");
            if (pair.length == 2) {
                map.put(pair[0], pair[1]);
            }
        }
        return map;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println("AdminMenuController: " + method + " " + path);

        // GET /api/admin/menu?restaurantId=123
        if ("GET".equalsIgnoreCase(method)) {
            URI uri = exchange.getRequestURI();
            String query = uri.getQuery();
            Map<String, String> params = parseQuery(query);
            if (params.containsKey("restaurantId")) {
                try {
                    int restaurantId = Integer.parseInt(params.get("restaurantId"));
                    List<MenuItem> items = menuItemDAO.findByRestaurantId(restaurantId);
                    sendResponse(exchange, 200, JsonUtils.toJson(items));
                    return;
                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"error\":\"Ongeldig restaurant ID\"}");
                    return;
                }
            } else {
                sendResponse(exchange, 400, "{\"error\":\"restaurantId parameter vereist\"}");
                return;
            }
        }

        // POST /api/admin/menu
        if ("POST".equalsIgnoreCase(method) && path.equals("/api/admin/menu")) {
            try {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                MenuItem item = JsonUtils.fromJson(body, MenuItem.class);
                boolean success = menuItemDAO.save(item);
                if (success) {
                    sendResponse(exchange, 201, "{\"message\":\"Menu-item toegevoegd\"}");
                } else {
                    sendResponse(exchange, 500, "{\"error\":\"Kon menu-item niet toevoegen\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
            return;
        }

        // PUT /api/admin/menu/5
        Pattern putPattern = Pattern.compile("/api/admin/menu/(\\d+)");
        Matcher putMatcher = putPattern.matcher(path);
        if ("PUT".equalsIgnoreCase(method) && putMatcher.matches()) {
            try {
                int id = Integer.parseInt(putMatcher.group(1));
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
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
            return;
        }

        // DELETE /api/admin/menu/5
        Matcher deleteMatcher = putPattern.matcher(path);
        if ("DELETE".equalsIgnoreCase(method) && deleteMatcher.matches()) {
            try {
                int id = Integer.parseInt(deleteMatcher.group(1));
                boolean success = menuItemDAO.delete(id);
                if (success) {
                    sendResponse(exchange, 200, "{\"message\":\"Menu-item verwijderd\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Menu-item niet gevonden\"}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"Server error\"}");
            }
            return;
        }

        sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}