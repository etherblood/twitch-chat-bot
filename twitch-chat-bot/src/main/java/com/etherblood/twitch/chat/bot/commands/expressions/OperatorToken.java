package com.etherblood.twitch.chat.bot.commands.expressions;

/**
 *
 * @author Philipp
 */
public enum OperatorToken implements ExpressionToken {
    ADD(2, Associativity.LEFT),
    SUBTRACT(2, Associativity.LEFT),
    MULTIPLY(3, Associativity.LEFT),
    DIVIDE(3, Associativity.LEFT);

    private final int precedence;
    private final Associativity associativity;

    private OperatorToken(int precedence, Associativity associativity) {
        this.precedence = precedence;
        this.associativity = associativity;
    }

    public int getPrecedence() {
        return precedence;
    }

    public Associativity getAssociativity() {
        return associativity;
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
