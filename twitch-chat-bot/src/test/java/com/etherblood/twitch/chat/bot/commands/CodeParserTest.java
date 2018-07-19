package com.etherblood.twitch.chat.bot.commands;

import java.sql.SQLException;
import java.time.Instant;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Philipp
 */
public class CodeParserTest {

    public CodeParserTest() {
    }

    @Test
    public void now() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withNowTag("now")
                .build();
        String code = "current millis: [now]<unused>[/now]";
        CommandContext context = new CommandContext();
        context.now = Instant.now();
        String text = parser.codeToText(code, context);
        assertEquals("current millis: " + context.now.toEpochMilli(), text);
    }

    @Test
    public void sender() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withSenderTag("sender")
                .build();
        String code = "sender name is: [sender]<unused>[/sender]";
        CommandContext context = new CommandContext();
        context.sender = new MockTwitchUser() {
            @Override
            public String getDisplayName() {
                return "senderName";
            }
        };
        String text = parser.codeToText(code, context);
        assertEquals("sender name is: " + context.sender.getDisplayName(), text);
    }

}
