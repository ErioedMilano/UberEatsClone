package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.MenuItemDAO;
import com.ubereats.dao.OrderDAO;
import com.ubereats.model.Order;
import com.ubereats.model.OrderItem;
import com.ubereats.model.OrderRequest;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class OrderController implements HttpHandler {

    private OrderDAO orderDAO = new OrderDAO();
    private MenuItemDAO menuItemDAO = new MenuItemDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        System.out.println("Request: " + method + " " + path); // Log de request

        // CORS headers
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        if ("OPTIONS".equalsIgnoreCase(method)) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if ("POST".equalsIgnoreCase(method) && path.equals("/api/orders")) {
            // Lees body
            InputStream is = exchange.getRequestBody();
            String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Body: " + body); // Log de body

            OrderRequest request = JsonUtils.fromJson(body, OrderRequest.class);
            System.out.println("Geparsed request: " + (request == null ? "null" : request.getCustomerName())); // Log parsed object

            if (request == null || request.getCustomerName() == null || request.getItems() == null) {
                System.out.println("Validatie mislukt: request=" + request + ", customerName=" + (request != null ? request.getCustomerName() : "N/A") + ", items=" + (request != null ? request.getItems() : "N/A"));
                String error = "{\"error\":\"Ongeldige aanvraag\"}";
                exchange.sendResponseHeaders(400, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
                return;
            }

            // Bereken totaal en maak Order object
            double total = 0.0;
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
                double price = menuItemDAO.getPrice(itemReq.getMenuItemId());
                if (price == 0) {
                    String error = "{\"error\":\"Menu-item niet gevonden: " + itemReq.getMenuItemId() + "\"}";
                    exchange.sendResponseHeaders(400, error.getBytes().length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(error.getBytes());
                    os.close();
                    return;
                }
                OrderItem item = new OrderItem();
                item.setMenuItemId(itemReq.getMenuItemId());
                item.setQuantity(itemReq.getQuantity());
                item.setPrice(price);
                orderItems.add(item);
                total += price * itemReq.getQuantity();
            }

            Order order = new Order();
            order.setCustomerName(request.getCustomerName());
            order.setCustomerAddress(request.getCustomerAddress());
            order.setTotal(total);

            int orderId = orderDAO.save(order, orderItems);

            if (orderId != -1) {
                String response = "{\"orderId\":" + orderId + ", \"message\":\"Bestelling geplaatst\"}";
                exchange.sendResponseHeaders(201, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String error = "{\"error\":\"Bestelling kon niet worden opgeslagen\"}";
                exchange.sendResponseHeaders(500, error.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(error.getBytes());
                os.close();
            }
        } else {
            String error = "{\"error\":\"Not found\"}";
            exchange.sendResponseHeaders(404, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }
}