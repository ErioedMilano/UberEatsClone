package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.AdminDAO;
import com.ubereats.dao.SessionDAO;
import com.ubereats.model.Admin;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class AdminAuthController implements HttpHandler {

    private AdminDAO adminDAO = new AdminDAO();
    private SessionDAO sessionDAO = new SessionDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(method) && path.equals("/api/admin/login")) {
            handleLogin(exchange);
        } else if ("POST".equalsIgnoreCase(method) && path.equals("/api/admin/logout")) {
            handleLogout(exchange);
        } else {
            sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        LoginRequest request = JsonUtils.fromJson(body, LoginRequest.class);

        if (request == null || request.username == null || request.password == null) {
            sendResponse(exchange, 400, "{\"error\":\"Ongeldige aanvraag\"}");
            return;
        }

        Admin admin = adminDAO.findByUsername(request.username);
        if (admin == null || !adminDAO.validatePassword(request.password, admin.getPasswordHash())) {
            sendResponse(exchange, 401, "{\"error\":\"Ongeldige gebruikersnaam of wachtwoord\"}");
            return;
        }

        String token = sessionDAO.createSession(admin.getId());
        if (token == null) {
            sendResponse(exchange, 500, "{\"error\":\"Kon sessie niet aanmaken\"}");
            return;
        }

        String response = "{\"token\":\"" + token + "\", \"adminId\":" + admin.getId() + "}";
        sendResponse(exchange, 200, response);
    }

    private void handleLogout(HttpExchange exchange) throws IOException {
        String token = extractToken(exchange);
        if (token != null) {
            sessionDAO.deleteSession(token);
        }
        sendResponse(exchange, 200, "{\"message\":\"Uitgelogd\"}");
    }

    private String extractToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    static class LoginRequest {
        String username;
        String password;
    }
}