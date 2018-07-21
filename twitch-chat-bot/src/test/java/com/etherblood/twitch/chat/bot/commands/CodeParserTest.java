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

    @Test
    public void math() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withMathTag("math")
                .build();
        String code = "result is: [math]5 - 7 * (8 + 3)[/math]";
        String text = parser.codeToText(code, null);
        assertEquals("result is: " + Long.toString(5 - 7 * (8 + 3)), text);
    }

    @Test
    public void math2() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withMathTag("math")
                .build();
        String code = "result is: [math]-15 / 3[/math]";
        String text = parser.codeToText(code, null);
        assertEquals("result is: " + Long.toString(-15 / 3), text);
    }

    @Test
    public void math3() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withMathTag("math")
                .build();
        String code = "result is: [math]-75--6+-3-+5[/math]";
        String text = parser.codeToText(code, null);
        assertEquals("result is: " + Long.toString(-75- -6+ -3- +5), text);
    }

    @Test
    public void math4() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withMathTag("math")
                .build();
        String code = "result is: [math]15/4[/math]";
        String text = parser.codeToText(code, null);
        assertEquals("result is: 3.75", text);
    }

    @Test
    public void math5() throws SQLException {
        CodeParser parser = new CodeParserBuilder()
                .withMathTag("math")
                .build();
        String code = "result is: [math]-5  /15[/math]";
        String text = parser.codeToText(code, null);
        assertEquals("result is: -0.333", text);
    }

}
