package com.etherblood.twitch.chat.bot;

import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class Command {

    public String id, code, author;
    public long useCount;
    public Instant lastUsed, lastModified;

    public Command(String id, String code, String author) {
        this(id, code, author, 0, null, Instant.now());
    }

    public Command(String id, String code, String author, long useCount, Instant lastUsed, Instant lastModified) {
        this.id = id;
        this.code = code;
        this.author = author;
        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.lastModified = lastModified;
    }
}
