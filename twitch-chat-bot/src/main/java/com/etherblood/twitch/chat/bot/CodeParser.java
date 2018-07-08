package com.etherblood.twitch.chat.bot;

import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Philipp
 */
public class CodeParser {

    private static final Pattern TAG_PATTERN = Pattern.compile("\\[([a-z]+?)(?:=(.+?))?\\]");
    private static final int TAG_GROUP = 1;
    private static final int ATTRIBUTE_GROUP = 2;

    private final Map<String, BinaryOperator<String>> tagStrategies;

    CodeParser(Map<String, BinaryOperator<String>> tagStrategies) {
        this.tagStrategies = tagStrategies;
    }

    public String codeToText(String code) {
        StringBuilder builder = new StringBuilder();
        int position = 0;
        Matcher matcher = TAG_PATTERN.matcher(code);
        while (matcher.find(position)) {
            String tag = matcher.group(TAG_GROUP);
            String attribute = matcher.group(ATTRIBUTE_GROUP);
            BinaryOperator<String> strategy = tagStrategies.get(tag);
            int start = matcher.start();
            int end = matcher.end();
            String tagText = strategy != null? strategy.apply(tag, attribute): code.substring(start, end);
            builder.append(code.substring(position, start));
            builder.append(tagText);
            position = end;
        }
        builder.append(code.substring(position));
        return builder.toString();
    }
}
