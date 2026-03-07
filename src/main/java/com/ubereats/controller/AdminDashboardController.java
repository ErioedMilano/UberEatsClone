package com.ubereats.controller;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.ubereats.dao.DashboardDAO;
import com.ubereats.model.DashboardResponse;
import com.ubereats.model.MenuItemStat;
import com.ubereats.model.RestaurantStat;
import com.ubereats.util.JsonUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class AdminDashboardController implements HttpHandler {

    private DashboardDAO dashboardDAO = new DashboardDAO();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        // GEEN CORS-HEADERS

        if ("GET".equalsIgnoreCase(method) && path.equals("/api/admin/dashboard")) {
            try {
                List<RestaurantStat> topRestaurants = dashboardDAO.getTopRestaurants(5);
                List<MenuItemStat> topDishes = dashboardDAO.getTopDishes(5);
                int totalOrdersToday = dashboardDAO.getTotalOrdersToday();
                double revenueToday = dashboardDAO.getRevenueToday();
                Map<String, Integer> ordersByHour = dashboardDAO.getOrdersByLast24Hours();

                DashboardResponse response = new DashboardResponse(topRestaurants, topDishes, totalOrdersToday, revenueToday, ordersByHour);
                String jsonResponse = JsonUtils.toJson(response);
                sendResponse(exchange, 200, jsonResponse);
            } catch (Exception e) {
                e.printStackTrace();
                sendResponse(exchange, 500, "{\"error\":\"Internal server error\"}");
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