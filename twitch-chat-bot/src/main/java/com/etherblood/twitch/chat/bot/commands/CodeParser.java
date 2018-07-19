package com.etherblood.twitch.chat.bot.commands;

import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Philipp
 */
public class CodeParser {

    private static final Pattern ANY_TAG_PATTERN = Pattern.compile("\\[\\/?([a-z0-9]+?)]");
    private static final int TAG_GROUP = 1;

    private final Map<String, CodeTagStrategy> tagStrategies;

    CodeParser(Map<String, CodeTagStrategy> tagStrategies) {
        this.tagStrategies = tagStrategies;
    }

    public String codeToText(String code, CommandContext context) throws SQLException {
        Deque<CodeTag> tags = new ArrayDeque<>();
        CodeTag root = new CodeTag(null, "");
        tags.addLast(root);
        int position = 0;
        Matcher matcher = ANY_TAG_PATTERN.matcher(code);
        while (matcher.find(position)) {
            boolean isOpenTag = code.charAt(matcher.start() + 1) != '/';
            tags.peekLast().body += code.substring(position, matcher.start());
            if (isOpenTag) {
                CodeTag openedTag = new CodeTag(matcher.group(TAG_GROUP), "");
                tags.addLast(openedTag);
            } else {
                CodeTag closedTag = tags.removeLast();
                if (!matcher.group(TAG_GROUP).equals(closedTag.type)) {
                    throw new AssertionError("tag mismatch " + matcher.group(TAG_GROUP) + " != " + closedTag.type + " in " + code);
                }
                tags.peekLast().body += tagStrategies.get(closedTag.type).handle(closedTag, context);
            }
            position = matcher.end();
        }
        tags.peekLast().body += code.substring(position);
        if (root != tags.removeLast()) {
            throw new AssertionError(code);
        }
        return root.body;
    }

}
