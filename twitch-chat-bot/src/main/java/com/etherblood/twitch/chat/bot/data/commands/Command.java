package com.etherblood.twitch.chat.bot.data.commands;

import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class Command {

    public Long id;
    public String alias;
    public String code;
    public String author;
    public long useCount;
    public Instant lastUsed, lastModified;

    public Command(String alias, String code, String author) {
        this(null, alias, code, author, 0, null, Instant.now());
    }

    public Command(Long id, String alias, String code, String author, long useCount, Instant lastUsed, Instant lastModified) {
        this.id = id;
        this.alias = alias;
        this.code = code;
        this.author = author;
        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.lastModified = lastModified;
    }
}
