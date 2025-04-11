package com.example;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Simple HTTP server that serves the calculator web interface
 * and provides a REST API for calculations.
 */
public class WebCalculatorServer {
    private static final int PORT = 8080;
    private static final String WEB_DIR = "web";

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);

        // API endpoint for calculations
        server.createContext("/api/calculate", new CalculateHandler());

        // Static file server for the web frontend
        server.createContext("/", new StaticFileHandler());

        server.setExecutor(null);
        server.start();

        System.out.println("Server started on port " + PORT);
        System.out.println("Open http://localhost:" + PORT + " in your browser");
    }

    static class CalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            try {
                // 1) Read request body in a Java 8–compatible way
                InputStream inputStream = exchange.getRequestBody();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] temp = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(temp)) != -1) {
                    buffer.write(temp, 0, bytesRead);
                }
                String requestBody = new String(buffer.toByteArray(), StandardCharsets.UTF_8);

                // 2) Parse JSON (simple parsing, for example only)
                Map<String, String> params = parseJsonRequest(requestBody);

                if (!params.containsKey("expression")) {
                    sendResponse(exchange, 400, "{\"error\":\"Missing expression parameter\"}");
                    return;
                }

                String expression = params.get("expression");

                // 3) Evaluate the expression
                try {
                    Integer result = CalculatorUtils.evaluate(expression);
                    String jsonResponse = "{\"result\":" + result + "}";
                    sendResponse(exchange, 200, jsonResponse);
                } catch (Exception e) {
                    sendResponse(exchange, 400, "{\"error\":\"" + e.getMessage() + "\"}");
                }
            } catch (Exception e) {
                sendResponse(exchange, 500, "{\"error\":\"Server error: " + e.getMessage() + "\"}");
            }
        }

        private Map<String, String> parseJsonRequest(String json) {
            Map<String, String> result = new HashMap<>();
            // Simple JSON parsing — in production use a real JSON library
            if (json.contains("\"expression\"")) {
                // Split around "expression"
                String[] split = json.split("\"expression\"");
                if (split.length > 1) {
                    // Then split again on quotation marks
                    String[] parts = split[1].split("\"");
                    // parts[0] = :,
                    // parts[1] should be the actual expression
                    if (parts.length > 1) {
                        result.put("expression", parts[1]);
                    }
                }
            }
            return result;
        }
    }

    static class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();

            // Default to index.html
            if (path.equals("/")) {
                path = "/index.html";
            }

            // Combine 'web' directory with the path requested
            Path filePath = Paths.get(WEB_DIR + path);

            // Security check: prevent directory traversal
            if (!filePath.normalize().startsWith(Paths.get(WEB_DIR))) {
                sendResponse(exchange, 403, "Forbidden");
                return;
            }

            try {
                // Read the file in a Java 8–compatible way
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                try (InputStream inputStream = Files.newInputStream(filePath)) {
                    byte[] data = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(data)) != -1) {
                        buffer.write(data, 0, bytesRead);
                    }
                }
                byte[] fileContent = buffer.toByteArray();

                // Determine the content type
                String contentType = getContentType(path);
                exchange.getResponseHeaders().set("Content-Type", contentType);

                // Send the file
                exchange.sendResponseHeaders(200, fileContent.length);
                OutputStream os = exchange.getResponseBody();
                os.write(fileContent);
                os.close();
            } catch (IOException e) {
                // File not found
                sendResponse(exchange, 404, "Not Found");
            }
        }

        private String getContentType(String path) {
            if (path.endsWith(".html")) return "text/html";
            if (path.endsWith(".css")) return "text/css";
            if (path.endsWith(".js")) return "application/javascript";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "text/plain";
        }
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response)
            throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        // For error or JSON responses, set JSON content type:
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}
