package com.etherblood.twitch.chat.bot.commands.expressions;

/**
 *
 * @author Philipp
 */
public class NumberToken implements ExpressionToken {

    private final long value;

    private NumberToken(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public static NumberToken of(String expression) {
        return new NumberToken(Long.parseLong(expression));
    }
}
