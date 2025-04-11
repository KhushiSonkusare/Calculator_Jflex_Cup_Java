package com.example;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class to enhance the expression evaluator with common operations
 * like caching and constant optimization
 */
public class ExpressionEvaluatorUtils {
    
    // Cache for frequently used expression results
    private static final Map<String, Integer> expressionCache = new ConcurrentHashMap<>();
    
    // Maximum cache size to prevent memory issues
    private static final int MAX_CACHE_SIZE = 1000;
    
    /**
     * Cache an expression result for future lookups
     */
    public static void cacheExpressionResult(String expression, Integer result) {
        if (expressionCache.size() >= MAX_CACHE_SIZE) {
            // Simple eviction strategy: clear half the cache when it fills up
            clearOldestEntries();
        }
        expressionCache.put(expression, result);
    }
    
    /**
     * Get a cached result for an expression
     * @return The cached result or null if not found
     */
    public static Integer getCachedResult(String expression) {
        return expressionCache.get(expression);
    }
    
    /**
     * Clear oldest entries from the cache
     */
    private static void clearOldestEntries() {
        // Simple approach: just clear the entire cache
        // In a production system, you might use a more sophisticated LRU approach
        expressionCache.clear();
    }
    
    /**
     * Optimize simple constant expressions before parsing
     * For expressions that don't need full parsing
     */
    public static Integer optimizeConstantExpression(String expression) {
        expression = expression.replaceAll("\\s+", "");
        
        try {
            // Try to directly parse as a single number
            return Integer.parseInt(expression);
        } catch (NumberFormatException e) {
            // Not a simple number, needs full parsing
            return null;
        }
    }
    
    /**
     * Check if an expression contains only numbers and operations
     * (no variables or functions that would require context)
     */
    public static boolean isConstantExpression(String expression) {
        return expression.matches("[0-9+\\-*/() ]+");
    }
}