package com.etherblood.twitch.chat.bot;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Philipp
 */
public class CommandHandler implements TwirkListener {

    private final Twirk twirk;
    private final CommandRepository repo;
    private final Set<String> reserved = new HashSet<>();
    private final CodeParser codeParser;

    public CommandHandler(Twirk twirk, CommandRepository repo, CodeParser codeParser) {
        this.twirk = twirk;
        this.repo = repo;
        this.codeParser = codeParser;
        reserved.add("set");
        reserved.add("commands");
        reserved.add("stack");
    }

    @Override
    public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
        try {
            String text = message.getContent();
            if (text.startsWith("!")) {
                String[] parts = text.substring(1).split(" ", 2);
                handleCommand(parts[0], parts.length == 2 ? parts[1] : "");
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        }
    }

    private void handleCommand(String command, String args) throws SQLException {
        switch (command) {
            case "set":
                setCommand(args);
                break;
            case "commands":
                listCommands(args);
                break;
            case "stack":
                stack(args);
                break;
            default:
                textCommand(command);
                break;
        }
    }

    private void textCommand(String command) throws SQLException {
        String code = repo.load(command);
        if(code == null) {
            return;
        }
        twirk.channelMessage(codeParser.codeToText(code));
    }

    private void stack(String command) {
        twirk.channelMessage("SnekHead");
        twirk.channelMessage("SNekBody");
        twirk.channelMessage("SnekFooter");
    }

    private void setCommand(String args) throws SQLException {
        String[] parts = args.split(" ", 2);
        String command = parts[0];
        if (!reserved.contains(command)) {
            String value = parts.length == 2 ? parts[1].trim() : "";
            if (value.isEmpty()) {
                repo.delete(command);
            } else {
                repo.save(command, value);
            }
        }
    }

    private void listCommands(String args) throws SQLException {
        int page;
        try {
            page = Integer.parseInt(args);
        } catch (NumberFormatException e) {
            page = 0;
        }
        String response = repo.getCommands(page, 10)
                .stream()
                .collect(Collectors.joining(" ", "commands page" + page + " [", "]."));
        twirk.channelMessage(response);
    }
}
