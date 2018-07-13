package com.etherblood.twitch.chat.bot;

import java.sql.SQLException;

/**
 *
 * @author Philipp
 */
public interface CommandConsumer {
    void consume(Context context) throws SQLException;
}
