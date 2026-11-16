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
 * Embedded Zero-Dependency HTTP Web Preview & PWA Server for the Bus Reservation System.
 * Powered 100% by core Java (com.sun.net.httpserver.HttpServer).
 * Serves live interactive HTML UI and PWA assets.
 */
public class WebPreviewServer {

    private static final int DEFAULT_PORT = 8085;

    public static void main(String[] args) throws IOException {
        DAOFactory.initialize();

        int port = DEFAULT_PORT;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.isBlank()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticAppHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("==================================================================");
        System.out.println(" BUS RESERVATION SYSTEM - LIVE WEB PREVIEW STARTED");
        System.out.println(" URL Link: http://localhost:" + port);
        System.out.println(" PWA Ready: Manifest & Service Worker Active");
        System.out.println("==================================================================");
    }

    private static class StaticAppHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.isBlank()) {
                path = "/index.html";
            }

            byte[] content = null;
            String contentType = "text/html; charset=UTF-8";

            if (path.equals("/manifest.json")) {
                contentType = "application/manifest+json; charset=UTF-8";
                content = loadAsset("manifest.json");
            } else if (path.equals("/sw.js")) {
                contentType = "application/javascript; charset=UTF-8";
                content = loadAsset("sw.js");
            } else if (path.endsWith(".html") || path.equals("/index.html")) {
                contentType = "text/html; charset=UTF-8";
                content = loadAsset("index.html");
            } else {
                // Fallback to index.html for SPA routing
                contentType = "text/html; charset=UTF-8";
                content = loadAsset("index.html");
            }

            if (content == null) {
                content = "<!DOCTYPE html><html><body><h2>SwiftBus Web Server Running</h2></body></html>".getBytes(StandardCharsets.UTF_8);
            }

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
    }

    private static byte[] loadAsset(String fileName) {
        // 1. Try disk path for rapid hot reloading
        Path diskPath = Path.of("src", "main", "resources", "web", fileName);
        if (Files.exists(diskPath)) {
            try {
                return Files.readAllBytes(diskPath);
            } catch (IOException ignored) {}
        }

        // 2. Fallback to classpath
        try (InputStream in = WebPreviewServer.class.getResourceAsStream("/web/" + fileName)) {
            if (in != null) {
                return in.readAllBytes();
            }
        } catch (IOException ignored) {}

        return null;
    }
}
