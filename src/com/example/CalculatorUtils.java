package com.example;

import java.io.StringReader;

/**
 * Utility class to separate evaluation logic for reuse in both CLI and GUI
 */
public class CalculatorUtils {
    
    /**
     * Evaluates a mathematical expression and returns the result
     * 
     * @param expression The expression to evaluate
     * @return The result of the evaluation
     * @throws Exception If there is an error in parsing or evaluation
     */
    public static Integer evaluate(String expression) throws Exception {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be empty");
        }
        
        try {
            Lexer lexer = new Lexer(new StringReader(expression.trim()));
            Parser parser = new Parser(lexer);
            return (Integer) parser.parse().value;
        } catch (Exception e) {
            // Enhance error message if possible
            String message = e.getMessage();
            if (message == null || message.isEmpty()) {
                message = "Invalid expression syntax";
            }
            throw new Exception("Calculation error: " + message, e);
        }
    }
    
    /**
     * Checks if an expression has valid syntax without evaluating it
     * 
     * @param expression The expression to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidExpression(String expression) {
        try {
            evaluate(expression);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Helper method to format the result for display
     */
    public static String formatResult(Integer result) {
        if (result == null) {
            return "Error";
        }
        return result.toString();
    }
}