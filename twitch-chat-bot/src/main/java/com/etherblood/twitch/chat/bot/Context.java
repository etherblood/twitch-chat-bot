package com.etherblood.twitch.chat.bot;

import com.etherblood.twitch.chat.bot.commands.Command;
import com.gikk.twirk.types.users.TwitchUser;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

/**
 *
 * @author Philipp
 */
public class Context {
    public Map<String, CommandConsumer> baseCommands;
    public String commandAlias;
    public Command command;
    public String commandArgs;
    public TwitchUser sender;
    public Instant now;
    public final Deque<Tag> tags = new ArrayDeque<>();
    
    public Tag tag() {
        return tags.getLast();
    }
    
}
