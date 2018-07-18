package com.etherblood.twitch.chat.bot;

import com.etherblood.twitch.chat.bot.commands.CommandRepository;
import com.etherblood.twitch.chat.bot.clips.ClipRepository;
import com.etherblood.twitch.chat.bot.commands.alias.CommandAliasRepository;
import com.etherblood.twitch.chat.bot.commands.tags.CommandTagRepository;
import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListener;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost/twitchbot", "twitchbot", "twitchbot");
        CommandRepository commands = new CommandRepository(connection);
        ClipRepository clips = new ClipRepository(connection);
        WhitelistRepository whitelist = new WhitelistRepository(connection);
        CommandAliasRepository aliases = new CommandAliasRepository(connection);
        CommandTagRepository tags = new CommandTagRepository(connection);

        String username = args[0];
        String oauth = args[1];
        String channel = "#" + args[2];
        Twirk twirk = new TwirkBuilder(channel, username, oauth)
                .build();
        CodeParser codeParser = new CodeParserBuilder()
                .withTimeTag("time")
                .withBracketTag("bracket")
                .withCommandTag("cmd")
                .withRegexTag("regex", 0)
                .withRegexTag("regex0", 0)
                .withRegexTag("regex1", 1)
                .withRegexTag("regex2", 2)
                .build();
        twirk.addIrcListener(new CommandHandler(twirk, commands, codeParser, whitelist, clips, aliases, tags));
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
