package com.busreservation.web;

import com.busreservation.dao.DAOFactory;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Embedded Zero-Dependency HTTP Web Preview Server for the Bus Reservation System.
 * Powered 100% by core Java (com.sun.net.httpserver.HttpServer).
 * Serves live interactive HTML UI from src/main/resources/web/index.html.
 */
public class WebPreviewServer {

    private static final int PORT = 8085;

    public static void main(String[] args) throws IOException {
        DAOFactory.initialize();
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/", new StaticAppHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("==================================================================");
        System.out.println(" BUS RESERVATION SYSTEM - LIVE WEB PREVIEW STARTED");
        System.out.println(" URL Link: http://localhost:" + PORT);
        System.out.println(" Features: User Login, Login Gate Before Booking, User Data Isolation");
        System.out.println("==================================================================");
    }

    private static class StaticAppHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            byte[] response = getHtmlContent().getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response);
            }
        }
    }

    private static String getHtmlContent() {
        // First check disk file for development and hot reloading
        Path diskPath = Path.of("src", "main", "resources", "web", "index.html");
        if (Files.exists(diskPath)) {
            try {
                return Files.readString(diskPath, StandardCharsets.UTF_8);
            } catch (IOException ignored) {}
        }

        // Fallback to classpath resource
        try (InputStream in = WebPreviewServer.class.getResourceAsStream("/web/index.html")) {
            if (in != null) {
                return new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (IOException ignored) {}

        return "<!DOCTYPE html><html><body><h2>SwiftBus Web Server Running</h2></body></html>";
    }
}
