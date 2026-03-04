package com.ubereats.server;

import com.sun.net.httpserver.HttpServer;
import com.ubereats.controller.OrderController;
import com.ubereats.controller.RestaurantController;
import com.ubereats.controller.MenuController;  // nieuwe import

import java.io.IOException;
import java.net.InetSocketAddress;

public class SimpleHttpServer {

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/restaurants", new RestaurantController());
        server.createContext("/api/restaurants/", new MenuController());  // belangrijk: de slash voor pad matching
        server.createContext("/api/orders", new OrderController());  // nieuw
        server.setExecutor(null);
        server.start();
        System.out.println("Server started on port 8080");
    }
}