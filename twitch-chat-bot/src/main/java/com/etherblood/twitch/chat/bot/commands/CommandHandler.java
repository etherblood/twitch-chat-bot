package com.etherblood.twitch.chat.bot.commands;

import com.etherblood.twitch.chat.bot.data.WhitelistRepository;
import com.etherblood.twitch.chat.bot.data.commands.CommandRepository;
import com.etherblood.twitch.chat.bot.data.commands.Command;
import com.etherblood.twitch.chat.bot.data.commands.tags.CommandTag;
import com.etherblood.twitch.chat.bot.data.commands.tags.CommandTagRepository;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Philipp
 */
public class CommandHandler implements TwirkListener {

    private final Twirk twirk;
    private final WhitelistRepository whitelist;
    private final CommandRepository commands;
    private final CommandTagRepository tags;
    private final Set<String> reserved = new HashSet<>();
    private final CodeParser codeParser;
    private final USER_TYPE minPrivilidge = USER_TYPE.MOD;

    public CommandHandler(Twirk twirk, CommandRepository commands, WhitelistRepository whitelist, CommandTagRepository tags) {
        this.twirk = twirk;
        this.commands = commands;
        this.codeParser = new CodeParserBuilder()
                .withTimeTag("time")
                .withNowTag("now")
                .withSenderTag("sender")
                .withBracketTag("bracket")
                .withMathTag("math")
                .withEchoTag("echo")
                .withRegexTag("regex", 0)
                .withRegexTag("regex0", 0)
                .withRegexTag("regex1", 1)
                .withRegexTag("regex2", 2)
                .withTagStrategy("set", this::setCommand)
                .withTagStrategy("permit", this::permit)
                .withTagStrategy("unpermit", this::unpermit)
                .withTagStrategy("tag", this::tag)
                .withTagStrategy("untag", this::untag)
                .withTagStrategy("list", this::list)
                .withTagStrategy("cmd", this::textCommand)
                .withTagStrategy("show", this::show)
                .withTagStrategy("tags", this::listTags)
                .build();
        this.whitelist = whitelist;
        this.tags = tags;
        reserved.add("set");
        reserved.add("permit");
        reserved.add("unpermit");
        reserved.add("tag");
        reserved.add("untag");
        reserved.add("list");
        reserved.add("cmd");
        reserved.add("show");
        reserved.add("tags");
    }

    @Override
    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
        try {
            String text = message.getContent();
            if (text.startsWith("!")) {
                CommandContext context = new CommandContext();
                context.twirk = twirk;
                context.now = Instant.now();
                context.sender = sender;
                String response = textCommand(new CodeTag(null, text.substring(1)), context).trim();
                if (!response.isEmpty()) {
                    twirk.channelMessage(response);
                }
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    private String unpermit(CodeTag tag, CommandContext context) throws SQLException {
        return whitelist(tag, context, false);
    }

    private String permit(CodeTag tag, CommandContext context) throws SQLException {
        return whitelist(tag, context, true);
    }

    private boolean isMod(CommandContext context) {
        return context.sender.getUserType().value >= minPrivilidge.value;
    }

    private String whitelist(CodeTag tag, CommandContext context, boolean value) throws SQLException {
        assertAccess(isMod(context));
        String user = tag.body.trim();
        if (user.startsWith("@")) {
            user = user.substring(1);
        }
        whitelist.setWhitelisted(user, value);
        return user + (whitelist.isWhitelisted(user) ? " is whitelisted" : " not whitelisted");
    }

    private String textCommand(CodeTag tag, CommandContext context) throws SQLException {
        String[] parts = tag.body.split(" ", 2);
        String commandAlias = parts[0];
        Command command = commands.load(commandAlias);
        try {
            context.commandArgs = parts.length == 2 ? parts[1] : "";
            return codeParser.codeToText(command.code, context);
        } finally {
            command.useCount++;
            command.lastUsed = context.now;
            commands.save(command);
        }
    }

    private String show(CodeTag tag, CommandContext context) throws SQLException {
        assertAccess(isMod(context) || whitelist.isWhitelisted(context.sender.getUserName()));
        Command command = commands.load(tag.body.trim());
        return command.code;
    }

    private String setCommand(CodeTag tag, CommandContext context) throws SQLException {
        assertAccess(isMod(context) || whitelist.isWhitelisted(context.sender.getUserName()));
        String[] parts = tag.body.split(" ", 2);
        String alias = parts[0];
        if (!isReserved(alias)) {
            String args = parts.length == 2 ? parts[1].trim() : "";
            if (args.isEmpty()) {
                    commands.delete(alias);
            } else {
                commands.save(new Command(alias, args, context.sender.getDisplayName()));
            }
        }
        return "";
    }

    private String tag(CodeTag tag, CommandContext context) throws SQLException {
        assertAccess(isMod(context) || whitelist.isWhitelisted(context.sender.getUserName()));
        String[] parts = tag.body.split(" ");
        String alias = parts[0];
        String tagName = parts.length >= 2 ? parts[1].trim() : "";
        Command command = commands.load(alias);
        if (command != null) {
            tags.save(new CommandTag(tagName, command.id));
        }
        return "";
    }

    private String untag(CodeTag tag, CommandContext context) throws SQLException {
        assertAccess(isMod(context) || whitelist.isWhitelisted(context.sender.getUserName()));
        String[] parts = tag.body.split(" ");
        String alias = parts[0];
        String tagName = parts.length >= 2 ? parts[1].trim() : "";
        Command command = commands.load(alias);
        if (command != null) {
            tags.delete(command.id, tagName);
        }
        return "";
    }

    private boolean isReserved(String alias) {
        return reserved.contains(alias.toLowerCase());
    }

    private String list(CodeTag tag, CommandContext context) throws SQLException {
        String listedTag = tag.body.trim();
        return tags.getCommands(listedTag)
                .stream()
                .collect(Collectors.joining(", ", listedTag + " [", "]."));
    }

    private String listTags(CodeTag tag, CommandContext context) throws SQLException {
        return tags.getTags()
                .stream()
                .collect(Collectors.joining(", ", "tags [", "]."));
    }

    private void assertAccess(boolean accessAllowed) {
        if (!accessAllowed) {
            throw new RuntimeException("access denied");
        }
    }

}
