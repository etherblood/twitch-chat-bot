package com.etherblood.twitch.chat.bot.commands;

import java.sql.SQLException;

/**
 *
 * @author Philipp
 */
public interface CodeTagStrategy {

    String handle(CodeTag tag, CommandContext context) throws SQLException;
}
