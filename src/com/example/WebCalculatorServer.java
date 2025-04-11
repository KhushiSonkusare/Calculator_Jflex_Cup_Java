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
 * and provides a REST API for calculations
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
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }
            
            try {
                // Parse request body
                String requestBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                Map<String, String> params = parseJsonRequest(requestBody);
                
                if (!params.containsKey("expression")) {
                    sendResponse(exchange, 400, "{\"error\":\"Missing expression parameter\"}");
                    return;
                }
                
                String expression = params.get("expression");
                
                // Calculate result
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
            // Simple JSON parsing - in production use a proper JSON library
            if (json.contains("\"expression\"")) {
                String expression = json.split("\"expression\"")[1];
                expression = expression.split("\"")[1];
                result.put("expression", expression);
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
            
            Path filePath = Paths.get(WEB_DIR + path);
            
            // Security check - prevent directory traversal attacks
            if (!filePath.normalize().startsWith(Paths.get(WEB_DIR))) {
                sendResponse(exchange, 403, "Forbidden");
                return;
            }
            
            try {
                byte[] fileContent = Files.readAllBytes(filePath);
                
                // Set content type based on file extension
                String contentType = getContentType(path);
                exchange.getResponseHeaders().set("Content-Type", contentType);
                
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
            if (path.endsWith(".html")) {
                return "text/html";
            } else if (path.endsWith(".css")) {
                return "text/css";
            } else if (path.endsWith(".js")) {
                return "application/javascript";
            } else if (path.endsWith(".ico")) {
                return "image/x-icon";
            }
            return "text/plain";
        }
    }
    
    private static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] responseBytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, responseBytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(responseBytes);
        os.close();
    }
}