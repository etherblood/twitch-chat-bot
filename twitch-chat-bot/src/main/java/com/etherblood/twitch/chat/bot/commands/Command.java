package com.etherblood.twitch.chat.bot.commands;

import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class Command {

    public Long id;
    public String code, author;
    public long useCount;
    public Instant lastUsed, lastModified;

    public Command(String code, String author) {
        this(null, code, author, 0, null, Instant.now());
    }

    public Command(Long id, String code, String author, long useCount, Instant lastUsed, Instant lastModified) {
        this.id = id;
        this.code = code;
        this.author = author;
        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.lastModified = lastModified;
    }
}
