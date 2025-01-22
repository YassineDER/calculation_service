package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {
        String operation = "1 / (45 + 3 * 2) / 12 - 1";
        System.out.println(calculate(operation)); // Should print "-0,998"
        
        operation = "1 / 0";
        System.out.println(calculate(operation)); // Should print "Error: Invalid expression"
    }

    public static String calculate(String operation) {
        // Replace commas with dots for decimal compatibility
        String modifiedOperation = operation.replace(",", ".");
        try {
            // Parse and evaluate the expression using the exp4j library
            Expression expression = new ExpressionBuilder(modifiedOperation).build();
            double result = expression.evaluate();

            // Format the result to 3 decimal places
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            return decimalFormat.format(result).replace(".", ",");
        } catch (Exception e) {
            return "Error: Invalid expression";
        }
    }
}