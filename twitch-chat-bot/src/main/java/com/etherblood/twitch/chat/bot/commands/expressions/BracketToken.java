package com.etherblood.twitch.chat.bot.commands.expressions;

/**
 *
 * @author Philipp
 */
public enum BracketToken implements ExpressionToken {
    LEFT, RIGHT;

    public static BracketToken of(String expression) {
        switch (expression) {
            case "(":
                return LEFT;
            case ")":
                return RIGHT;
            default:
                throw new AssertionError(expression);
        }
    }
}
