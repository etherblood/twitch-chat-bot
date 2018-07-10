package com.etherblood.twitch.chat.bot;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 *
 * @author Philipp
 */
public class CodeParserBuilder {

    private final Map<String, Function<Context, String>> tagStrategies = new HashMap<>();

    public CodeParserBuilder withTimeTag(String tag) {
        tagStrategies.put(tag, CodeParserBuilder::parseTime);
        return this;
    }

    public CodeParserBuilder withBracketTag(String tag) {
        tagStrategies.put(tag, x -> "[");
        return this;
    }

    public CodeParserBuilder withRegexTag(String tag, int groupIndex) {
        tagStrategies.put(tag, x -> parseRegex(x, groupIndex));
        return this;
    }
    
    private static String parseRegex(Context context, int groupIndex) {
        Pattern pattern = Pattern.compile(context.tagAttribute);
        Matcher matcher = pattern.matcher(context.commandArgs);
        if(matcher.find()) {
            return context.commandArgs.substring(matcher.start(groupIndex), matcher.end(groupIndex));
        }
        return "[error]";
    }

    private static String parseTime(Context context) {
        Instant time;
        try {
            long epochMilli = Long.parseLong(context.tagAttribute);
            time = Instant.ofEpochMilli(epochMilli);
        } catch (NumberFormatException e) {
            time = Instant.parse(context.tagAttribute);
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
        list.add(days + "days");
        list.add(hours + "hours");
        list.add(minutes + "min");
        list.add(seconds + "sec");
        return list.stream().filter(s -> !s.startsWith("0")).limit(2).collect(Collectors.joining(" "));
    }

    public CodeParserBuilder withTagStrategy(String tag, Function<Context, String> strategy) {
        tagStrategies.put(tag, strategy);
        return this;
    }

    public CodeParser build() {
        return new CodeParser(new HashMap<>(tagStrategies));
    }
}
