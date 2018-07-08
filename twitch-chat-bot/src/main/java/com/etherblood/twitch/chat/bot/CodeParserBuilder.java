package com.etherblood.twitch.chat.bot;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

/**
 *
 * @author Philipp
 */
public class CodeParserBuilder {

    private final Map<String, BinaryOperator<String>> tagStrategies = new HashMap<>();

    public CodeParserBuilder withTimeTag(String tag) {
        tagStrategies.put(tag, CodeParserBuilder::parseTime);
        return this;
    }

    public CodeParserBuilder withBracketTag(String tag) {
        tagStrategies.put(tag, (t, a) -> "[");
        return this;
    }

    private static String parseTime(String tag, String attribute) {
        Instant time;
        try {
            long epochMilli = Long.parseLong(attribute);
            time = Instant.ofEpochMilli(epochMilli);
        } catch (NumberFormatException e) {
            time = Instant.parse(attribute);
        }
        Duration duration = Duration.between(time, Instant.now());
        return humanReadableFormat(duration);
    }

    private static String humanReadableFormat(Duration duration) {
        long totalSeconds = duration.getSeconds();
        long days = TimeUnit.SECONDS.toDays(totalSeconds);
        long hours = TimeUnit.SECONDS.toHours(totalSeconds - TimeUnit.DAYS.toSeconds(days));
        long minutes = TimeUnit.SECONDS.toMinutes(totalSeconds - TimeUnit.HOURS.toSeconds(hours));
        long seconds = totalSeconds - TimeUnit.MINUTES.toSeconds(minutes);
        List<String> list = new ArrayList<>();
        list.add(days + "days");
        list.add(hours + "hours");
        list.add(minutes + "min");
        list.add(seconds + "sec");
        return list.stream().filter(s -> !s.startsWith("0")).limit(2).collect(Collectors.joining(" "));
    }

    public CodeParserBuilder withTagStrategy(String tag, BinaryOperator<String> strategy) {
        tagStrategies.put(tag, strategy);
        return this;
    }

    public CodeParser build() {
        return new CodeParser(new HashMap<>(tagStrategies));
    }
}
