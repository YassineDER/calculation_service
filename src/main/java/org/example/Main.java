package org.example;

import java.util.ArrayDeque;
import java.util.Deque;

public class Main {
    
    public static void main(String[] args) {
        System.out.println(calculate("1 / (45 + 3 * 2) / 12 - 1")); // Expected: -0,998
        System.out.println(calculate("(2 + 3) * (4 / 2)"));         // Expected: 10,000
        
        try {
            System.out.println(calculate("10 / 0"));                    // Expected: Error: Division by zero
            System.out.println(calculate("5 % 0"));                     // Expected: Error: Division by zero
            System.out.println(calculate(""));                          // Expected: Error: Invalid expression
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
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
            return "Invalid expression";
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

        StringBuilder decimals = new StringBuilder(roundedStr.substring(dotIndex + 1));
        while (decimals.length() < 3) 
            decimals.append("0"); // Add trailing zeros
        
        return roundedStr.substring(0, dotIndex) + "," + decimals;
    }

    private static double evaluate(String expression) {
        Deque<Double> values = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();
        int len = expression.length();

        int i = 0;
        while (i < len) {
            char c = expression.charAt(i);

            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c) || c == '.') {
                int newIndex = processNumber(expression, len, i, values);
                i = newIndex; // Update index after processing the number
            } else if (c == '(') {
                operators.push(c);
            } else if (c == ')') {
                processClosingParenthesis(values, operators);
            } else if (isOperator(c)) {
                processOperator(c, values, operators);
            }
            i++; // Increment loop counter only once here
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    private static int processNumber(String expression, int len, int index, Deque<Double> values) {
        StringBuilder sb = new StringBuilder();
        while (index < len && (Character.isDigit(expression.charAt(index)) || expression.charAt(index) == '.')) {
            sb.append(expression.charAt(index));
            index++;
        }
        values.push(Double.parseDouble(sb.toString()));
        return index - 1; // Return the last processed index
    }

    private static void processClosingParenthesis(Deque<Double> values, Deque<Character> operators) {
        while (!operators.isEmpty() && operators.peek() != '(') {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }
        if (!operators.isEmpty()) 
            operators.pop(); // Pop '('
    }

    private static void processOperator(char operator, Deque<Double> values, Deque<Character> operators) {
        while (!operators.isEmpty() && precedence(operator) <= precedence(operators.peek())) 
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        
        operators.push(operator);
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
                if (b == 0) throw new ArithmeticException("Division by zero");
                return a / b;
            case '%':
                if (b == 0) throw new ArithmeticException("Modulo by zero");
                return a % b;
            default:
                throw new UnsupportedOperationException("Invalid operator");
        }
    }
}