package com.etherblood.twitch.chat.bot.commands;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.types.users.TwitchUser;
import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class CommandContext {

    public Twirk twirk;
    public String commandArgs;
    public TwitchUser sender;
    public Instant now;

}
