package com.etherblood.twitch.chat.bot.commands.expressions;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author Philipp
 */
public class ExpressionEvaluator {

    private static final String NUMBER_PATTERN = "[-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?";
    private static final String OPERATOR_PATTERN = "\\+|\\-|\\*|\\/";
    private static final String BRACKET_PATTERN = "\\(|\\)";
    private static final Pattern SYMBOL_PATTERN = Pattern.compile(Stream.of(NUMBER_PATTERN, OPERATOR_PATTERN, BRACKET_PATTERN).collect(Collectors.joining(")|(", "(", ")")));
    private static final int NUMBER_GROUP = 1;
    private static final int OPERATOR_GROUP = 2;
    private static final int BRACKET_GROUP = 3;

    private static final DecimalFormat FORMAT = (DecimalFormat) NumberFormat.getNumberInstance(Locale.ENGLISH);

    public String eval(String expression) {
        List<ExpressionToken> tokens = toTokens(expression);
        Deque<ExpressionToken> postfix = toPostfix(tokens);
        double result = evaluatePostfix(postfix);
        return FORMAT.format(result);
    }

    private double evaluatePostfix(Deque<ExpressionToken> postfix) throws AssertionError {
        Deque<Double> stack = new ArrayDeque<>();
        for (ExpressionToken token : postfix) {
            if (token instanceof NumberToken) {
                stack.addLast(((NumberToken) token).getValue());
            } else {
                ((OperatorToken) token).apply(stack);
            }
        }
        return stack.pop();
    }

    private Deque<ExpressionToken> toPostfix(List<ExpressionToken> tokens) {
        Deque<ExpressionToken> output = new ArrayDeque<>();
        Deque<ControlToken> operators = new ArrayDeque<>();
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
                            && (((OperatorToken) operators.peekLast()).getPrecedence() > operator.getPrecedence()
                            || (((OperatorToken) operators.peekLast()).getPrecedence() == operator.getPrecedence()
                            && ((OperatorToken) operators.peekLast()).getAssociativity() == OperatorToken.Associativity.LEFT))) {
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
            if (matcher.group(NUMBER_GROUP) != null) {
                tokens.add(NumberToken.of(matcher.group(NUMBER_GROUP)));
            } else if (matcher.group(OPERATOR_GROUP) != null) {
                tokens.add(OperatorToken.of(matcher.group(OPERATOR_GROUP)));
            } else if (matcher.group(BRACKET_GROUP) != null) {
                tokens.add(BracketToken.of(matcher.group(BRACKET_GROUP)));
            }

            position = matcher.end();
        }
        return tokens;
    }
}
