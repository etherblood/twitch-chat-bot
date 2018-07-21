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

    private static final String NUMBER_PATTERN = "([-+]?[0-9]*\\.?[0-9]+(?:[eE][-+]?[0-9]+)?)";
    private static final String OPERATOR_PATTERN = "(\\+|\\-|\\*|\\/)";
    private static final String BRACKET_PATTERN = "(\\(|\\))";
    private static final Pattern NUMBER_OR_BRACKET = Pattern.compile(Stream.of(NUMBER_PATTERN, BRACKET_PATTERN).collect(Collectors.joining("|")));
    private static final Pattern OPERATOR_OR_BRACKET = Pattern.compile(Stream.of(OPERATOR_PATTERN, BRACKET_PATTERN).collect(Collectors.joining("|")));

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
        Matcher numberOrBracket = NUMBER_OR_BRACKET.matcher(expression);
        Matcher operatorOrBracket = OPERATOR_OR_BRACKET.matcher(expression);
        Matcher matcher = numberOrBracket;
        int position = 0;
        while (matcher.find(position)) {
            position = matcher.end();
            if (matcher.group(1) != null) {
                if (matcher == numberOrBracket) {
                    tokens.add(NumberToken.of(matcher.group(1)));
                } else {
                    tokens.add(OperatorToken.of(matcher.group(1)));
                }
                matcher = matcher == numberOrBracket ? operatorOrBracket : numberOrBracket;
            } else if (matcher.group(2) != null) {
                tokens.add(BracketToken.of(matcher.group(2)));
            }
        }
        return tokens;
    }
}
