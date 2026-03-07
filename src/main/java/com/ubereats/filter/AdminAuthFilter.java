package com.ubereats.filter;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.SessionDAO;

import java.io.IOException;
import java.io.OutputStream;

public class AdminAuthFilter implements HttpHandler {
    private final HttpHandler nextHandler;
    private final SessionDAO sessionDAO = new SessionDAO();

    public AdminAuthFilter(HttpHandler nextHandler) {
        this.nextHandler = nextHandler;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // CORS preflight
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        // Token ophalen uit header
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            sendUnauthorized(exchange);
            return;
        }
        String token = authHeader.substring(7);
        if (sessionDAO.validateToken(token) == null) {
            sendUnauthorized(exchange);
            return;
        }

        // Voeg CORS header toe voor de eigenlijke response
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        nextHandler.handle(exchange);
    }

    private void sendUnauthorized(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        String response = "{\"error\":\"Unauthorized\"}";
        exchange.sendResponseHeaders(401, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}