package com.etherblood.twitch.chat.bot.data;

import java.sql.Timestamp;
import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class Util {

    public static Timestamp toTimestamp(Instant instant) {
        if (instant == null) {
            return null;
        }
        return Timestamp.from(instant);
    }

    public static Instant toInstant(Timestamp timestamp) {
        if (timestamp == null) {
            return null;
        }
        return timestamp.toInstant();
    }
}
