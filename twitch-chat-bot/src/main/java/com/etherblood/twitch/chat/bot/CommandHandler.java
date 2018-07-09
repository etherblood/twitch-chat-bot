package com.etherblood.twitch.chat.bot;

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
    private final CommandRepository repo;
    private final Set<String> reserved = new HashSet<>();
    private final CodeParser codeParser;
    private final USER_TYPE minPrivilidge = USER_TYPE.MOD;

    public CommandHandler(Twirk twirk, CommandRepository repo, CodeParser codeParser, WhitelistRepository whitelist) {
        this.twirk = twirk;
        this.repo = repo;
        this.codeParser = codeParser;
        this.whitelist = whitelist;
        reserved.add("set");
        reserved.add("commands");
        reserved.add("stack");
        reserved.add("permit");
        reserved.add("unpermit");
    }

    @Override
    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
        try {
            String text = message.getContent();
            if (text.startsWith("!")) {
                String[] parts = text.substring(1).split(" ", 2);
                Context context = new Context();
                context.commandId = parts[0];
                context.commandArgs = parts.length == 2 ? parts[1] : "";
                context.now = Instant.now();
                context.sender = sender;
                handleCommand(context);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    private void handleCommand(Context context) throws SQLException {
        boolean isAtLeastMod = context.sender.getUserType().value >= minPrivilidge.value;
        switch (context.commandId) {
            case "set":
                if (isAtLeastMod || whitelist.isWhitelisted(context.sender.getUserName())) {
                    setCommand(context);
                }
                break;
            case "commands":
                listCommands(context);
                break;
            case "stack":
                stack(context);
                break;
            case "permit":
                if (isAtLeastMod) {
                    whitelist(context, true);
                }
                break;
            case "unpermit":
                if (isAtLeastMod) {
                    whitelist(context, false);
                }
                break;
            default:
                textCommand(context);
                break;
        }
    }

    private void whitelist(Context context, boolean value) throws SQLException {
        String user = context.commandArgs.trim();
        whitelist.setWhitelisted(user, value);
        twirk.channelMessage(user + (whitelist.isWhitelisted(user) ? " is whitelisted" : " not whitelisted"));
    }

    private void textCommand(Context context) throws SQLException {
        context.command = repo.load(context.commandId);
        if (context.command == null) {
            return;
        }
        twirk.channelMessage(codeParser.codeToText(context));
        context.command.useCount++;
        context.command.lastUsed = context.now;
        repo.save(context.command);
    }

    private void stack(Context context) {
        twirk.channelMessage("SnekHead");
        twirk.channelMessage("SNekBody");
        twirk.channelMessage("SnekFooter");
    }

    private void setCommand(Context context) throws SQLException {
        String[] parts = context.commandArgs.split(" ", 2);
        String commandId = parts[0];
        if (!reserved.contains(commandId)) {
            String args = parts.length == 2 ? parts[1].trim() : "";
            if (args.isEmpty()) {
                repo.delete(commandId);
            } else {
                repo.save(new Command(commandId, args, context.sender.getDisplayName()));
            }
        }
    }

    private void listCommands(Context context) throws SQLException {
        int page;
        try {
            page = Integer.parseInt(context.commandArgs);
        } catch (NumberFormatException e) {
            page = 0;
        }
        String response = repo.getCommands(page, 25)
                .stream()
                .collect(Collectors.joining(" ", "commands page" + page + " [", "]."));
        twirk.channelMessage(response);
    }
}
