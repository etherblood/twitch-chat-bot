package com.etherblood.twitch.chat.bot.commands.expressions;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Philipp
 */
public class ExpressionEvaluator {

    private static final Pattern SYMBOL_PATTERN = Pattern.compile("([0-9]+)|(\\+|\\-|\\*|\\/)|(\\(|\\))");
    private static final int LONG_GROUP = 1;
    private static final int OPERATOR_GROUP = 2;
    private static final int PARENTHESIS_GROUP = 3;

    public String eval(String expression) {
        List<ExpressionToken> tokens = toTokens(expression);
        Deque<ExpressionToken> postfix = toPostfix(tokens);
        long result = evaluatePostfix(postfix);
        return Long.toString(result);
    }

    private long evaluatePostfix(Deque<ExpressionToken> postfix) throws AssertionError {
        Deque<Long> stack = new ArrayDeque<>();
        for (ExpressionToken token : postfix) {
            if (token instanceof NumberToken) {
                stack.addLast(((NumberToken) token).getValue());
            } else {
                long b = stack.removeLast();
                long a = stack.removeLast();
                long result;
                switch ((OperatorToken) token) {
                    case ADD:
                        result = a + b;
                        break;
                    case SUBTRACT:
                        result = a - b;
                        break;
                    case MULTIPLY:
                        result = a * b;
                        break;
                    case DIVIDE:
                        result = a / b;
                        break;
                    default:
                        throw new AssertionError(token);
                }
                stack.addLast(result);
            }
        }
        long result = stack.pop();
        return result;
    }

    private Deque<ExpressionToken> toPostfix(List<ExpressionToken> tokens) {
        Deque<ExpressionToken> output = new ArrayDeque<>();
        Deque<ExpressionToken> operators = new ArrayDeque<>();
        for (ExpressionToken token : tokens) {
            if (token instanceof NumberToken) {
                output.add(token);
            } else {
                if (token == BracketToken.LEFT) {
                    operators.addLast(BracketToken.LEFT);
                } else if (token == BracketToken.RIGHT) {
                    while (operators.peekLast() != BracketToken.LEFT) {
                        output.add(operators.removeLast());
                    }
                    operators.removeLast();
                } else {
                    OperatorToken operator = (OperatorToken) token;
                    while (!operators.isEmpty()
                            && operators.peekLast() != BracketToken.LEFT
                            && (((OperatorToken)operators.peekLast()).getPrecedence() > operator.getPrecedence()
                            || (((OperatorToken)operators.peekLast()).getPrecedence() == operator.getPrecedence()
                            && ((OperatorToken)operators.peekLast()).getAssociativity() == OperatorToken.Associativity.LEFT))                            ) {
                        output.add(operators.removeLast());
                    }
                    operators.addLast(operator);
                }
            }
        }
        while (!operators.isEmpty()) {
            output.add(operators.removeLast());
        }
        return output;
    }

    private List<ExpressionToken> toTokens(String expression) {
        List<ExpressionToken> tokens = new ArrayList<>();
        Matcher matcher = SYMBOL_PATTERN.matcher(expression);
        int position = 0;
        while (matcher.find(position)) {
            if (matcher.group(LONG_GROUP) != null) {
                tokens.add(NumberToken.of(matcher.group(LONG_GROUP)));
            } else if (matcher.group(OPERATOR_GROUP) != null) {
                tokens.add(OperatorToken.of(matcher.group(OPERATOR_GROUP)));
            } else if (matcher.group(PARENTHESIS_GROUP) != null) {
                tokens.add(BracketToken.of(matcher.group(PARENTHESIS_GROUP)));
            }

            position = matcher.end();
        }
        return tokens;
    }
}
