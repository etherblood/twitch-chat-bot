package com.etherblood.twitch.chat.bot.data.commands.alias;

import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class CommandAlias {

    public String alias;
    public long commandId;
    public String author;
    public long useCount;
    public Instant lastUsed, lastModified;

    public CommandAlias(String alias, long commandId, String author) {
        this(alias, commandId, author, 0, null, Instant.now());
    }

    public CommandAlias(String alias, long commandId, String author, long useCount, Instant lastUsed, Instant lastModified) {
        this.alias = alias;
        this.commandId = commandId;
        this.author = author;
        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.lastModified = lastModified;
    }
}
