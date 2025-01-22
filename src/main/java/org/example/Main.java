package org.example;

import java.util.Stack;

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
            // Evaluate the expression and format the result
            double result = evaluate(modifiedOperation);
            String resultString = formatToThreeDecimals(result);
            return resultString.replace(".", ",");
        } catch (Exception e) {
            return "Error: Invalid expression";
        }
    }

    private static String formatToThreeDecimals(double value) {
        long scaled = Math.round(value * 1000); // Scale to 3 decimal places
        double rounded = scaled / 1000.0; // Convert back to double
        String roundedStr = Double.toString(rounded);

        // Ensure 3 decimal places by appending zeros if needed
        int dotIndex = roundedStr.indexOf(".");
        if (dotIndex == -1) {
            return roundedStr + ",000"; // No decimal point, add .000
        }

        String decimals = roundedStr.substring(dotIndex + 1);
        while (decimals.length() < 3) {
            decimals += "0"; // Add trailing zeros
        }
        return roundedStr.substring(0, dotIndex) + "," + decimals;
    }

    private static double evaluate(String expression) {
        // Convert to tokens and handle precedence
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();
        int len = expression.length();

        for (int i = 0; i < len; i++) {
            char c = expression.charAt(i);

            // Skip whitespace
            if (c == ' ') continue;

            // If digit, parse the number
            if (Character.isDigit(c) || c == '.') {
                StringBuilder sb = new StringBuilder();
                while (i < len && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    sb.append(expression.charAt(i++));
                }
                i--; // Adjust for loop increment
                values.push(Double.parseDouble(sb.toString()));
            }
            // Handle opening parenthesis
            else if (c == '(') {
                operators.push(c);
            }
            // Handle closing parenthesis
            else if (c == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop(); // Pop '('
            }
            // Handle operators
            else if (isOperator(c)) {
                while (!operators.isEmpty() && precedence(c) <= precedence(operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(c);
            }
        }

        // Apply remaining operators
        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '%';
    }

    private static int precedence(char op) {
        if (op == '+' || op == '-') return 1;
        if (op == '*' || op == '/' || op == '%') return 2;
        return 0;
    }

    private static double applyOperator(char op, double b, double a) {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                return a / b;
            case '%':
                return a % b;
            default:
                throw new UnsupportedOperationException("Invalid operator");
        }
    }
}