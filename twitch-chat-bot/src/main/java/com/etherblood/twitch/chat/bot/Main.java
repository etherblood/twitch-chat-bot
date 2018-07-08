package com.etherblood.twitch.chat.bot;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;

/**
 *
 * @author Philipp
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String... args) throws IOException, InterruptedException, SQLException {
        CommandRepository commands = new CommandRepository("jdbc:postgresql://localhost/twitchbot", "twitchbot", "twitchbot");

        String username = args[0];
        String oauth = args[1];
        String channel = "#" + args[2];
        Twirk twirk = new TwirkBuilder(channel, username, oauth)
                .build();
        CodeParser codeParser = new CodeParserBuilder()
                .withTimeTag("time")
                .withBracketTag("bracket")
                .withArgumentTag("arg")
                .build();
        twirk.addIrcListener(new CommandHandler(twirk, commands, codeParser));
        twirk.addIrcListener(new TwirkListener() {
            @Override
            public void onDisconnect() {
                try {
                    main(args);
                } catch (IOException | InterruptedException | SQLException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        });
        twirk.connect();
        System.out.println("Started at " + Instant.now());
    }

}
