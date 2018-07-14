package com.etherblood.twitch.chat.bot.commands.alias;

/**
 *
 * @author Philipp
 */
public class CommandAlias {

    public String alias;
    public long commandId;

    public CommandAlias(String alias, long commandId) {
        this.alias = alias;
        this.commandId = commandId;
    }

    public String getAlias() {
        return alias;
    }

    public long getCommandId() {
        return commandId;
    }
}
