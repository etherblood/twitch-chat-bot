package com.etherblood.twitch.chat.bot.clips;

import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class Clip {

    public String id, code, author;
    public long useCount;
    public Instant lastUsed, lastModified;

    public Clip(String id, String code, String author) {
        this(id, code, author, 0, null, Instant.now());
    }

    public Clip(String id, String code, String author, long useCount, Instant lastUsed, Instant lastModified) {
        this.id = id;
        this.code = code;
        this.author = author;
        this.useCount = useCount;
        this.lastUsed = lastUsed;
        this.lastModified = lastModified;
    }
}
