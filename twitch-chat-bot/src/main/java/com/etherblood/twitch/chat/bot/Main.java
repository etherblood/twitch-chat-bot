package com.etherblood.twitch.chat.bot;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        String username = "etherblood_ai";
        String oauth = args[0];
        String channel = "#serpent_ai_labs";
        Twirk twirk = new TwirkBuilder(channel, username, oauth)
                .setVerboseMode(true)
                .build();
        twirk.connect();
        CodeParser codeParser = new CodeParserBuilder().withTimeTag("time").build();
        twirk.addIrcListener(new CommandHandler(twirk, commands, codeParser));
        twirk.addIrcListener(new TwirkListener() {
            @Override
            public void onDisconnect() {
                try {
                    main();
                } catch (IOException | InterruptedException | SQLException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        });
    }

}
