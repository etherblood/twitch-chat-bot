package com.etherblood.twitch.chat.bot.commands.expressions;

import java.util.Deque;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;

/**
 *
 * @author Philipp
 */
public enum OperatorToken implements ControlToken {
    ADD(2, Associativity.LEFT, binaryOperation((a, b) -> a + b)),
    SUBTRACT(2, Associativity.LEFT, binaryOperation((a, b) -> a - b)),
    MULTIPLY(3, Associativity.LEFT, binaryOperation((a, b) -> a * b)),
    DIVIDE(3, Associativity.LEFT, binaryOperation((a, b) -> a / b));

    private final int precedence;
    private final Associativity associativity;
    private final Consumer<Deque<Double>> operation;

    private OperatorToken(int precedence, Associativity associativity, Consumer<Deque<Double>> operation) {
        this.precedence = precedence;
        this.associativity = associativity;
        this.operation = operation;
    }

    public int getPrecedence() {
        return precedence;
    }

    public Associativity getAssociativity() {
        return associativity;
    }
    
    public void apply(Deque<Double> stack) {
        operation.accept(stack);
    }

    private static Consumer<Deque<Double>> binaryOperation(BinaryOperator<Double> operation) {
        return stack -> {
            double b = stack.removeLast();
            double a = stack.removeLast();
            stack.addLast(operation.apply(a, b));
        };
    }

    public static OperatorToken of(String expression) {
        switch (expression) {
            case "+":
                return ADD;
            case "-":
                return SUBTRACT;
            case "*":
                return MULTIPLY;
            case "/":
                return DIVIDE;
            default:
                throw new AssertionError(expression);
        }
    }

    public enum Associativity {
        LEFT, RIGHT;
    }
}
