package com.etherblood.twitch.chat.bot.data.commands.tags;

/**
 *
 * @author Philipp
 */
public class CommandTag {

    public String tag;
    public long commandId;

    public CommandTag(String tag, long commandId) {
        this.tag = tag;
        this.commandId = commandId;
    }

    public String getTag() {
        return tag;
    }

    public long getCommandId() {
        return commandId;
    }
}
