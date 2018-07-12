package com.etherblood.twitch.chat.bot;

import com.gikk.twirk.types.users.TwitchUser;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 *
 * @author Philipp
 */
public class Context {
    public String commandId;
    public Command command;
    public String commandArgs;
    public TwitchUser sender;
    public Instant now;
    public final Deque<Tag> tags = new ArrayDeque<>();
    
    public Tag tag() {
        return tags.getLast();
    }
    
}
