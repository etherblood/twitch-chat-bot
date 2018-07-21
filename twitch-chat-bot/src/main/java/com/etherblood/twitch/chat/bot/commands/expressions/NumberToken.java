package com.etherblood.twitch.chat.bot.commands.expressions;

/**
 *
 * @author Philipp
 */
public class NumberToken implements ExpressionToken {

    private final double value;

    private NumberToken(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    public static NumberToken of(String expression) {
        return new NumberToken(Double.parseDouble(expression));
    }

    @Override
    public String toString() {
        return "#" + value;
    }
}
