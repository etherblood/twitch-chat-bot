package com.etherblood.twitch.chat.bot;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.enums.USER_TYPE;
import com.gikk.twirk.events.TwirkListener;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
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
    private final Map<String, CommandConsumer> baseCommands = new HashMap<>();
    private final CodeParser codeParser;
    private final USER_TYPE minPrivilidge = USER_TYPE.MOD;

    public CommandHandler(Twirk twirk, CommandRepository commands, CodeParser codeParser, WhitelistRepository whitelist, ClipRepository clips) {
        this.twirk = twirk;
        this.commands = commands;
        this.clips = clips;
        this.codeParser = codeParser;
        this.whitelist = whitelist;
        baseCommands.put("set", this::setCommand);
        baseCommands.put("commands", this::listCommands);
        baseCommands.put("clips", this::listClips);
        baseCommands.put("setclip", this::setClip);
        baseCommands.put("stack", this::stack);
        baseCommands.put("permit", this::permit);
        baseCommands.put("unpermit", this::unpermit);
        baseCommands.put("cobalt", this::cobalt);
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
        baseCommands.getOrDefault(context.commandId, this::textCommand).consume(context);
    }

    private void unpermit(Context context) throws SQLException {
        if (hasWritePrivilege(context)) {
            whitelist(context, false);
        }
    }

    private void permit(Context context) throws SQLException {
        if (hasWritePrivilege(context)) {
            whitelist(context, true);
        }
    }

    private boolean hasWritePrivilege(Context context) {
        return context.sender.getUserType().value >= minPrivilidge.value;
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

    private void cobalt(Context context) {
        twirk.channelMessage("cobalt1 cobalt2");
        twirk.channelMessage("cobalt3 cobalt4");
    }

    private void setCommand(Context context) throws SQLException {
        if (hasWritePrivilege(context) || whitelist.isWhitelisted(context.sender.getUserName())) {
            String[] parts = context.commandArgs.split(" ", 2);
            String commandId = parts[0];
            if (!baseCommands.containsKey(commandId)) {
                String args = parts.length == 2 ? parts[1].trim() : "";
                if (args.isEmpty()) {
                    commands.delete(commandId);
                } else {
                    commands.save(new Command(commandId, args, context.sender.getDisplayName()));
                }
            }
        }
    }

    private void setClip(Context context) throws SQLException {
        if (hasWritePrivilege(context) || whitelist.isWhitelisted(context.sender.getUserName())) {
            String[] parts = context.commandArgs.split(" ", 2);
            String clipId = parts[0];
            String args = parts.length == 2 ? parts[1].trim() : "";
            if (args.isEmpty()) {
                clips.delete(clipId);
            } else {
                clips.save(new Clip(clipId, args, context.sender.getDisplayName()));
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
