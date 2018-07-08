package com.etherblood.twitch.chat.bot;

import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Philipp
 */
public class CodeParser {

    private static final Pattern TAG_PATTERN = Pattern.compile("\\[([a-z0-9]+?)(?:=(.+?))?\\]");
    private static final int TAG_GROUP = 1;
    private static final int ATTRIBUTE_GROUP = 2;

    private final Map<String, Function<Context, String>> tagStrategies;

    CodeParser(Map<String, Function<Context, String>> tagStrategies) {
        this.tagStrategies = tagStrategies;
    }

    public String codeToText(Context context) {
        String code = context.command.code;
        StringBuilder builder = new StringBuilder();
        int position = 0;
        Matcher matcher = TAG_PATTERN.matcher(code);
        while (matcher.find(position)) {
            context.tag = matcher.group(TAG_GROUP);
            context.tagAttribute = matcher.group(ATTRIBUTE_GROUP);
            Function<Context, String> strategy = tagStrategies.get(context.tag);
            int start = matcher.start();
            int end = matcher.end();
            String tagText = strategy != null? strategy.apply(context): code.substring(start, end);
            builder.append(code.substring(position, start));
            builder.append(tagText);
            position = end;
        }
        builder.append(code.substring(position));
        return builder.toString();
    }
}
