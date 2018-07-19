package com.etherblood.twitch.chat.bot.commands;

import com.gikk.twirk.types.users.TwitchUser;
import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class CommandContext {

    public String commandArgs;
    public TwitchUser sender;
    public Instant now;

}
