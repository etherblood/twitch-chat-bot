package com.etherblood.twitch.chat.bot.commands;

import com.etherblood.twitch.chat.bot.commands.expressions.ExpressionEvaluator;
import com.etherblood.twitch.chat.bot.commands.expressions.NumberToken;
import com.etherblood.twitch.chat.bot.commands.expressions.OperatorToken;
import com.etherblood.twitch.chat.bot.commands.expressions.BracketToken;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import com.etherblood.twitch.chat.bot.commands.expressions.ExpressionToken;

/**
 *
 * @author Philipp
 */
public class CodeParserBuilder {

    private final Map<String, CodeTagStrategy> tagStrategies = new HashMap<>();

    public CodeParserBuilder withTimeTag(String tagName) {
        tagStrategies.put(tagName, CodeParserBuilder::parseTime);
        return this;
    }

    public CodeParserBuilder withSenderTag(String tagName) {
        tagStrategies.put(tagName, (tag, context) -> context.sender.getDisplayName());
        return this;
    }

    public CodeParserBuilder withNowTag(String tagName) {
        tagStrategies.put(tagName, (tag, context) -> Long.toString(context.now.toEpochMilli()));
        return this;
    }

    public CodeParserBuilder withBracketTag(String tagName) {
        tagStrategies.put(tagName, (tag, context) -> "[");
        return this;
    }

    public CodeParserBuilder withMathTag(String tagName) {
        tagStrategies.put(tagName, CodeParserBuilder::eval);
        return this;
    }

    private static String eval(CodeTag tag, CommandContext context) {
        return new ExpressionEvaluator().eval(tag.body);
    }

    public CodeParserBuilder withRegexTag(String tagName, int groupIndex) {
        tagStrategies.put(tagName, (tag, context) -> parseRegex(tag, context, groupIndex));
        return this;
    }

    private static String parseRegex(CodeTag tag, CommandContext context, int groupIndex) {
        Pattern pattern = Pattern.compile(tag.body);
        Matcher matcher = pattern.matcher(context.commandArgs);
        if (matcher.find()) {
            return matcher.group(groupIndex);
        }
        return "<error>";
    }

    private static String parseTime(CodeTag tag, CommandContext context) {
        Instant time;
        try {
            long epochMilli = Long.parseLong(tag.body);
            time = Instant.ofEpochMilli(epochMilli);
        } catch (NumberFormatException e) {
            time = Instant.parse(tag.body);
        }
        Duration duration = Duration.between(time, Instant.now());
        return humanReadableFormat(duration);
    }

    private static String humanReadableFormat(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long days = TimeUnit.SECONDS.toDays(totalSeconds);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds) % 24;
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds) % 60;
        long seconds = totalSeconds % 60;
        List<String> list = new ArrayList<>();
        list.add(days + (days == 1 ? "day" : "days"));
        list.add(hours + (hours == 1 ? "hour" : "hours"));
        list.add(minutes + "min");
        list.add(seconds + "sec");
        return list.stream().filter(s -> !s.startsWith("0")).limit(2).collect(Collectors.joining(" "));
    }

    public CodeParserBuilder withTagStrategy(String tag, CodeTagStrategy strategy) {
        tagStrategies.put(tag, strategy);
        return this;
    }

    public CodeParser build() {
        return new CodeParser(new HashMap<>(tagStrategies));
    }
}
