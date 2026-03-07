package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.OrderDAO;
import com.ubereats.model.Order;
import com.ubereats.model.OrderItem;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminOrderController implements HttpHandler {

    private OrderDAO orderDAO = new OrderDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            System.out.println("AdminOrderController: " + method + " " + path);

            Pattern orderIdPattern = Pattern.compile("/api/admin/orders/(\\d+)");
            Pattern orderItemsPattern = Pattern.compile("/api/admin/orders/(\\d+)/items");
            Matcher orderIdMatcher = orderIdPattern.matcher(path);
            Matcher orderItemsMatcher = orderItemsPattern.matcher(path);

            if ("GET".equalsIgnoreCase(method) && path.equals("/api/admin/orders")) {
                List<Order> orders = orderDAO.findAll();
                String response = JsonUtils.toJson(orders);
                sendResponse(exchange, 200, response);
            } else if ("GET".equalsIgnoreCase(method) && orderItemsMatcher.matches()) {
                int orderId = Integer.parseInt(orderItemsMatcher.group(1));
                List<OrderItem> items = orderDAO.findItemsByOrderId(orderId);
                String response = JsonUtils.toJson(items);
                sendResponse(exchange, 200, response);
            } else if ("DELETE".equalsIgnoreCase(method) && orderIdMatcher.matches()) {
                int id = Integer.parseInt(orderIdMatcher.group(1));
                boolean deleted = orderDAO.delete(id);
                if (deleted) {
                    sendResponse(exchange, 200, "{\"message\":\"Order verwijderd\"}");
                } else {
                    sendResponse(exchange, 404, "{\"error\":\"Order niet gevonden\"}");
                }
            } else {
                sendResponse(exchange, 404, "{\"error\":\"Not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            String error = "{\"error\":\"Internal server error: " + e.getMessage() + "\"}";
            sendResponse(exchange, 500, error);
        }
    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}