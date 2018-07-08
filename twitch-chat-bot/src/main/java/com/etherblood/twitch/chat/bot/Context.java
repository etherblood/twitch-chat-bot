package com.etherblood.twitch.chat.bot;

import com.gikk.twirk.types.users.TwitchUser;
import java.time.Instant;

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
    public String tag, tagAttribute;
    
}
