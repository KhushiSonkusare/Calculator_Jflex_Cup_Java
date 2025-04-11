package com.example;

import java.io.StringReader;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Optimized calculator application with better error handling and performance
 */
public class Main {
    private static final String EXIT_COMMAND = "exit";
    private static final String PROMPT = "Enter expression (or 'exit' to quit): ";
    private static final String RESULT_PREFIX = "Result: ";
    private static final String ERROR_PREFIX = "Error: ";
    
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            final AtomicBoolean running = new AtomicBoolean(true);
            
            // Add shutdown hook for proper termination
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                running.set(false);
                System.out.println("\nShutting down calculator...");
            }));
            
            while (running.get()) {
                System.out.print(PROMPT);
                String input = scanner.nextLine().trim();
                
                if (EXIT_COMMAND.equalsIgnoreCase(input)) {
                    break;
                }
                
                if (input.isEmpty()) {
                    continue;
                }
                
                evaluateExpression(input);
            }
        }
    }
    
    private static void evaluateExpression(String input) {
        try {
            long startTime = System.nanoTime();
            
            Lexer lexer = new Lexer(new StringReader(input));
            Parser parser = new Parser(lexer);
            Integer result = (Integer) parser.parse().value;
            
            long endTime = System.nanoTime();
            double executionTime = (endTime - startTime) / 1_000_000.0; // Convert to ms
            
            System.out.println(RESULT_PREFIX + result + " (computed in " + executionTime + " ms)");
        } catch (Exception e) {
            System.err.println(ERROR_PREFIX + (e.getMessage() != null ? e.getMessage() : "Unknown parsing error"));
        } finally {
            System.out.println();
        }
    }
}