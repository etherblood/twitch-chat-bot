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
    private final CommandRepository commands;
    private final ClipRepository clips;
    private final Set<String> reserved = new HashSet<>();
    private final CodeParser codeParser;
    private final USER_TYPE minPrivilidge = USER_TYPE.MOD;

    public CommandHandler(Twirk twirk, CommandRepository commands, CodeParser codeParser, WhitelistRepository whitelist, ClipRepository clips) {
        this.twirk = twirk;
        this.commands = commands;
        this.clips = clips;
        this.codeParser = codeParser;
        this.whitelist = whitelist;
        reserved.add("set");
        reserved.add("commands");
        reserved.add("clips");
        reserved.add("setclip");
        reserved.add("clip");
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
            case "setclip":
                if (isAtLeastMod || whitelist.isWhitelisted(context.sender.getUserName())) {
                    setClip(context);
                }
                break;
            case "commands":
                listCommands(context);
                break;
            case "clips":
                listClips(context);
                break;
            case "clip":
                getClip(context);
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
        context.command = commands.load(context.commandId);
        if (context.command == null) {
            return;
        }
        twirk.channelMessage(codeParser.codeToText(context));
        context.command.useCount++;
        context.command.lastUsed = context.now;
        commands.save(context.command);
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
                commands.delete(commandId);
            } else {
                commands.save(new Command(commandId, args, context.sender.getDisplayName()));
            }
        }
    }

    private void setClip(Context context) throws SQLException {
        String[] parts = context.commandArgs.split(" ", 2);
        String clipId = parts[0];
        String args = parts.length == 2 ? parts[1].trim() : "";
        if (args.isEmpty()) {
            clips.delete(clipId);
        } else {
            clips.save(new Clip(clipId, args, context.sender.getDisplayName()));
        }
    }

    private void listCommands(Context context) throws SQLException {
        int page;
        try {
            page = Integer.parseInt(context.commandArgs);
        } catch (NumberFormatException e) {
            page = 0;
        }
        String response = commands.getCommands(page, 25)
                .stream()
                .collect(Collectors.joining(", ", "commands page" + page + " [", "]."));
        twirk.channelMessage(response);
    }

    private void listClips(Context context) throws SQLException {
        int page;
        try {
            page = Integer.parseInt(context.commandArgs);
        } catch (NumberFormatException e) {
            page = 0;
        }
        String response = clips.getClips(page, 25)
                .stream()
                .collect(Collectors.joining(", ", "clips page" + page + " [", "]."));
        twirk.channelMessage(response);
    }

    private void getClip(Context context) throws SQLException {
        Clip clip = clips.load(context.commandArgs);
        if (clip == null) {
            return;
        }
        twirk.channelMessage(clip.code);
        clip.useCount++;
        clip.lastUsed = context.now;
        clips.save(clip);
    }
}
